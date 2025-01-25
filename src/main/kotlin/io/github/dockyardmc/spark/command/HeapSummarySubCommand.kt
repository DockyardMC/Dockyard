package io.github.dockyardmc.spark.command

import cz.lukynka.prettylog.log
import io.github.dockyardmc.commands.BrigadierStringType
import io.github.dockyardmc.commands.Command
import io.github.dockyardmc.commands.CommandExecutor
import io.github.dockyardmc.commands.StringArgument
import io.github.dockyardmc.scheduler.runAsync
import io.github.dockyardmc.spark.SparkDockyardIntegration
import me.lucko.spark.common.activitylog.Activity
import me.lucko.spark.common.command.sender.CommandSender
import me.lucko.spark.common.heapdump.HeapDumpSummary
import me.lucko.spark.common.util.MediaTypes
import java.nio.file.Files

class HeapSummarySubCommand(val spark: SparkDockyardIntegration): SparkSubCommand {

    private val platform = spark.platform

    override fun register(command: Command) {
        command.addSubcommand("heapsummary") {
            addOptionalArgument("arguments", StringArgument(BrigadierStringType.GREEDY_PHRASE))
            execute { ctx ->
                val arguments = getArgumentOrNull<String>("arguments")?.split(" ") ?: listOf()

                spark.broadcastPrefixed("Running garbage collector before creating heap summary..")
                System.gc()

                spark.broadcastPrefixed("Creating a new heap dump summary, please wait...")

                val heapDump: HeapDumpSummary
                try {
                    heapDump = HeapDumpSummary.createNew()
                } catch (exception: Exception) {
                    spark.broadcastPrefixed("<red>An error occurred whilst inspecting the heap: $exception")
                    log(exception)
                    return@execute
                }

                val player = ctx.getPlayerOrThrow()
                val output = heapDump.toProto(platform, CommandSender.Data(player.username, player.uuid))

                var shouldSaveToFile = false
                if(arguments.contains("--save-to-file")) {
                    shouldSaveToFile = true
                } else {
                    try {
                        runAsync {
                            val key = platform.bytebinClient.postContent(output, MediaTypes.SPARK_HEAP_MEDIA_TYPE).key()
                            val url = platform.viewerUrl + key

                            sendUrl(ctx, true, url)
                        }

                    } catch (exception: Exception) {
                        spark.broadcastPrefixed("<red>An error occurred whilst uploading the data. Attempting to save to disk instead.")
                        log(exception)
                        shouldSaveToFile = true
                    }
                }

                if(shouldSaveToFile) {
                    val file = platform.resolveSaveFile("heapsummary", "sparkheap")
                    try {
                        Files.write(file, output.toByteArray())
                        spark.broadcastPrefixed("Heap summary written to: <aqua>$file")
                        platform.activityLog.addToLog(Activity.fileActivity(CommandSender.Data(player.username, player.uuid), System.currentTimeMillis(), "Heap dump summary", file.toString()))
                    } catch (exception: Exception) {
                        spark.broadcastPrefixed("<red>An error occurred whilst saving the data to a file: $exception")
                        log(exception)
                    }
                }
            }
        }
    }

    private fun sendUrl(ctx: CommandExecutor, broadcast: Boolean, url: String) {
        val message = "<lime>Heap dump summary: <aqua><u><hover:show_text:'<gray>Click to open in browser: <yellow>$url'><click:open_url:'$url'>$url"
        if (broadcast) spark.broadcastPrefixed(message) else ctx.sendMessage(spark.prefixed(message))
    }
}