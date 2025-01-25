package io.github.dockyardmc.spark.command

import cz.lukynka.prettylog.log
import io.github.dockyardmc.commands.Command
import io.github.dockyardmc.scheduler.runAsync
import io.github.dockyardmc.spark.SparkDockyardIntegration
import me.lucko.spark.common.activitylog.Activity
import me.lucko.spark.common.command.sender.CommandSender
import me.lucko.spark.common.heapdump.HeapDump

class HeapDumpSubCommand(val spark: SparkDockyardIntegration): SparkSubCommand {

    private val platform = spark.platform

    override fun register(command: Command) {

        command.addSubcommand("heapdump") {
            execute { ctx ->
                val file = platform.resolveSaveFile("heap", if (HeapDump.isOpenJ9()) "phd" else "hprof")
                spark.broadcastPrefixed("Running garbage collector before creating heap summary..")
                System.gc()

                spark.broadcastPrefixed("Creating a new heap dump, please wait...")
                val player = ctx.getPlayerOrThrow()

                try {
                    runAsync {
                        HeapDump.dumpHeap(file, true)
                        spark.broadcastPrefixed("Heap drump written to <aqua> $file")

                        platform.activityLog.addToLog(Activity.fileActivity(CommandSender.Data(player.username, player.uuid), System.currentTimeMillis(), "Heap dump", file.toString()))
                    }
                } catch (exception: Exception) {
                    spark.broadcastPrefixed("<red>An error occurred whilst creating a heap dump: $exception")
                    log(exception)
                }
            }
        }
    }
}