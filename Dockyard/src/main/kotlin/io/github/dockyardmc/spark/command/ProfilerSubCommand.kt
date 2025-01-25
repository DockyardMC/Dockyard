package io.github.dockyardmc.spark.command

import cz.lukynka.prettylog.log
import io.github.dockyardmc.commands.*
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.scheduler.runAsync
import io.github.dockyardmc.spark.SparkDockyardIntegration
import me.lucko.bytesocks.client.BytesocksClient
import me.lucko.spark.common.activitylog.Activity
import me.lucko.spark.common.command.sender.CommandSender
import me.lucko.spark.common.sampler.*
import me.lucko.spark.common.sampler.Sampler.ExportProps
import me.lucko.spark.common.sampler.java.MergeStrategy
import me.lucko.spark.common.sampler.source.ClassSourceLookup
import me.lucko.spark.common.util.FormatUtil
import me.lucko.spark.common.util.MediaTypes
import me.lucko.spark.common.ws.ViewerSocket
import java.nio.file.Files

class ProfilerSubCommand(val spark: SparkDockyardIntegration) : SparkSubCommand {

    private val platform = spark.platform

    private val profileSubCommands = listOf("info", "open", "start", "stop", "cancel")
    private fun suggestProfilerSubCommands(player: Player): Collection<String> = profileSubCommands

    override fun register(command: Command) {
        command.addSubcommand("profiler") {
            addArgument("argument", StringArgument(), ::suggestProfilerSubCommands)
            addOptionalArgument("arguments", StringArgument(BrigadierStringType.GREEDY_PHRASE))

            execute { ctx ->
                val argument = getArgument<String>("argument")
                val arguments = getArgumentOrNull<String>("arguments")?.split(" ") ?: listOf()
                if (!profileSubCommands.contains(argument)) throw CommandException("$argument is not valid profiler argument!")
                when (argument) {
                    "info" -> profilerInfo(ctx)
                    "open" -> profilerOpen(ctx)
                    "cancel" -> profilerCancel(ctx)
                    "stop" -> profilerStop(ctx, arguments)
                    "start" -> profilerStart(ctx)
                    else -> throw CommandException("$argument not implemented yet")
                }
            }
        }
    }

    private fun profilerOpen(ctx: CommandExecutor) {
        val bytesocksClient = spark.platform.bytesocksClient
        val sampler = getActiveSamplerOrThrow()

        if (bytesocksClient == null) {
            ctx.sendMessage(spark.prefixed("<red>The live viewer is not supported."))
            return
        }

        val props = getExportProps(ctx)
        handleOpen(ctx, bytesocksClient, props, sampler)
    }

    private fun profilerInfo(ctx: CommandExecutor) {
        val sampler = getActiveSamplerOrThrow()

        ctx.sendMessage(" ")
        val runningTime = (System.currentTimeMillis() - sampler.startTime) / 1000L

        if (sampler.isRunningInBackground) {

            ctx.sendMessage(spark.prefixed("<lime>Profiler is already running!"))
            ctx.sendMessage(spark.prefixed("It was started <white>automatically <gray>when spark enabled and has been running for <aqua>${FormatUtil.formatSeconds(runningTime)}"))
        } else {
            ctx.sendMessage(spark.prefixed("So far, it has profiled for: <aqua>${FormatUtil.formatSeconds(runningTime)}"))
        }
        ctx.sendMessage(" ")
    }

    private fun profilerStart(ctx: CommandExecutor) {
        val previousSampler = platform.samplerContainer.activeSampler
        if (previousSampler != null) {
            if (previousSampler.isRunningInBackground) {
                // there is bg profiler running - stop that first
                ctx.sendMessage(spark.prefixed("Stopping the background profiler before starting... please wait"))
                previousSampler.stop(true)
                platform.samplerContainer.unsetActiveSampler(previousSampler)
            } else {
                profilerInfo(ctx)
                return
            }
        }

        val mode = SamplerMode.EXECUTION
        val threadDumper = ThreadDumper.ALL
        val threadGrouper = ThreadGrouper.BY_POOL
        val interval = SamplerMode.EXECUTION.defaultInterval().toDouble()
        val ignoreSleeping = false
        val forceJavaSampler = false
        val allocLiveOnly = false

        spark.broadcastPrefixed("Starting a new profiler, please wait...")

        val samplerBuilder = SamplerBuilder()
        samplerBuilder.mode(mode)
        samplerBuilder.threadDumper(threadDumper)
        samplerBuilder.threadGrouper(threadGrouper)
        samplerBuilder.samplingInterval(interval)
        samplerBuilder.ignoreSleeping(ignoreSleeping)
        samplerBuilder.forceJavaSampler(forceJavaSampler)
        samplerBuilder.allocLiveOnly(allocLiveOnly)

        var sampler: Sampler? = null
        try {
            sampler = samplerBuilder.start(platform)
        } catch (exception: Exception) {
            ctx.sendMessage(spark.prefixed("<red>${exception.message}"))
            return
        }

        platform.samplerContainer.activeSampler = sampler
        spark.broadcastPrefixed("<lime>Profiler is now running!")
        spark.broadcastPrefixed("It will run in the background until it is stopped by an admin.")

        val future = sampler.future

        future.whenCompleteAsync { s, throwable ->
            if(throwable != null) {
                spark.broadcastPrefixed("<red>Profiler operation failed unexpectedly. Error: $throwable")
                log(throwable as Exception)
            }
        }

        sampler.future.whenCompleteAsync { s, throwable ->
            platform.samplerContainer.unsetActiveSampler(s)
        }
    }

    private fun profilerCancel(ctx: CommandExecutor) {
        platform.samplerContainer.stopActiveSampler(true)
        spark.broadcastPrefixed("<red>Profiler has been cancelled.")
    }

    private fun profilerStop(ctx: CommandExecutor, args: List<String>) {
        val sampler = getActiveSamplerOrThrow()

        val saveToFile = args.contains("--save-to-file")
        if (saveToFile) {
            ctx.sendMessage(spark.prefixed("Stopping the profiler & saving results, please wait..."))
        } else {
            ctx.sendMessage(spark.prefixed("Stopping the profiler & uploading results, please wait..."))
        }

        val exportProps = getExportProps(ctx)
        handleUpload(ctx, exportProps, saveToFile)

        platform.samplerContainer.unsetActiveSampler(sampler)
        sampler.stop(false)

        if (platform.backgroundSamplerManager.restartBackgroundSampler()) {
            ctx.sendMessage(spark.prefixed("Automatically restarted the background profiler"))
        }
    }

    private fun handleUpload(ctx: CommandExecutor, exportProps: ExportProps, saveToFile: Boolean) {
        var shouldSaveToFile = saveToFile
        val sampler = getActiveSamplerOrThrow()
        try {
            runAsync {
                val output = sampler.toProto(platform, exportProps)
                val key = platform.bytebinClient.postContent(output, MediaTypes.SPARK_SAMPLER_MEDIA_TYPE).key()
                val url = platform.viewerUrl + key

                sendUrl(ctx, true, url)

                platform.activityLog.addToLog(Activity.urlActivity(CommandSender.Data(ctx.player!!.username, ctx.player.uuid), System.currentTimeMillis(), "Profiler", url))
            }
        } catch (exception: Exception) {
            ctx.sendMessage(spark.prefixed("<red>An error occurred while uploading the profiler results. Attempting to save to disk instead."))
            shouldSaveToFile = true
            log(exception)
        }

        if (shouldSaveToFile) {
            val file = platform.resolveSaveFile("profile", "sparkprofile")
            try {
                runAsync {
                    val output = sampler.toProto(platform, exportProps)

                    Files.write(file, output.toByteArray())
                    ctx.sendMessage(spark.prefixed("Profiler stopped & save complete!"))
                    ctx.sendMessage(spark.prefixed("Data has been written to: <aqua>$file"))
                }
            } catch (exception: Exception) {
                ctx.sendMessage(spark.prefixed("<red>An error occurred whilst saving the data."))
            }
        }
    }

    private fun getExportProps(ctx: CommandExecutor): Sampler.ExportProps {
        return Sampler.ExportProps()
            .creator(CommandSender.Data(ctx.player!!.username, ctx.player.uuid))
            .comment(null)
            .mergeStrategy(MergeStrategy.SEPARATE_PARENT_CALLS)
            .classSourceLookup { ClassSourceLookup.create(spark.platform) }
    }

    private fun handleOpen(ctx: CommandExecutor, bytesocksClient: BytesocksClient, props: ExportProps, sampler: Sampler) {
        spark.broadcastPrefixed("<gray>Loading live profiler viewer..")
        runAsync {
            val socket = ViewerSocket(spark.platform, bytesocksClient, props)
            sampler.attachSocket(socket)

            props.channelInfo(socket.payload)

            val data = sampler.toProto(spark.platform, props)
            val key: String = spark.platform.bytebinClient.postContent(data, MediaTypes.SPARK_SAMPLER_MEDIA_TYPE, "live").key()
            val url: String = spark.platform.viewerUrl + key

            sendUrl(ctx, true, url)
        }
    }

    private fun getActiveSamplerOrThrow(): Sampler {
        return platform.samplerContainer.activeSampler ?: throw CommandException("The profiler isn't running! To start a new one, run: <white>/spark profiler start")
    }

    private fun sendUrl(ctx: CommandExecutor, broadcast: Boolean, url: String) {
        val message = "<lime>Live profiler viewer: <aqua><u><hover:show_text:'<gray>Click to open in browser: <yellow>$url'><click:open_url:'$url'>$url"
        if (broadcast) spark.broadcastPrefixed(message) else ctx.sendMessage(spark.prefixed(message))
    }
}