package io.github.dockyardmc.spark

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.spark.command.ProfilerSubCommand

class SparkCommand(val spark: SparkDockyardIntegration) {

    private data class SparkCommandHelp(val name: String, val arguments: List<String> = listOf())

    private val commands = mutableListOf<SparkCommandHelp>(
        SparkCommandHelp("profiler info"),
        SparkCommandHelp("profiler open"),
        SparkCommandHelp("profiler start"),
        SparkCommandHelp("profiler stop"),
        SparkCommandHelp("profiler cancel"),
        SparkCommandHelp("tps"),
        SparkCommandHelp("ping", listOf("--player <dark_gray><username>")),
        SparkCommandHelp("healthreport"),
        SparkCommandHelp("tickmonitor", listOf("--without-gc")),
        SparkCommandHelp("gc"),
        SparkCommandHelp("gcmonitor"),
        SparkCommandHelp("heapsummary", listOf("--save-to-file")),
        SparkCommandHelp("heapdump", listOf("--compress <dark_gray><type>")),
    )

    private val helpMessage = buildString {
        commands.forEach { command ->
            appendLine("<orange><bold>></bold> <gray>/spark ${command.name}")
            if(command.arguments.isNotEmpty()) {
                command.arguments.forEach { arg ->
                    appendLine("    <dark_gray>[<gray>$arg<dark_gray>]")
                }
            }
        }
    }

    fun register() {
        Commands.add("/spark") {
            ProfilerSubCommand(spark).register(this)

            addSubcommand("help") {
                execute { ctx ->
                    helpMessage.split("\n").forEach { line ->
                        ctx.sendMessage(line)
                    }
                }
            }

            execute { ctx ->
                ctx.sendMessage(" ")
                ctx.sendMessage(spark.prefixed("<lime>spark <gray>v${spark.version} by <aqua>lucko<gray>. DockyardMC integration v${DockyardServer.versionInfo.dockyardVersion}"))
                ctx.sendMessage(spark.prefixed("Run <white>/spark help <gray>to view usage information."))
                ctx.sendMessage(" ")
            }
        }
    }
}