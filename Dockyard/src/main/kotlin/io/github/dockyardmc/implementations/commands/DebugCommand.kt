package io.github.dockyardmc.implementations.commands

import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.WorldArgument
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.extentions.toScrollText
import io.github.dockyardmc.world.World

class DebugCommand {

    init {

        Commands.add("/debug") {

            addSubcommand("world") {
                addArgument("world", WorldArgument())
                execute {
                    val world = getArgument<World>("world")
                    val message = buildString {
                        append("\n")
                        appendLine(" <gray>Entities: <yellow>${world.entities.size}")
                        appendLine(" <gray>Custom data blocks: <yellow>${world.customDataBlocks.size}")
                        appendLine(" <gray>Chunks in memory: <aqua>${world.chunks.size}")
                        appendLine(" <gray>Event pool size: <aqua>${world.eventPool.eventList().size}")
                        appendLine(" <gray>Scheduler running: ${(!world.scheduler.paused.value).toScrollText()}")
                        appendLine(" <gray>Scheduler tick rate: <lime>${world.scheduler.tickRate.value.inWholeMilliseconds}ms")
                    }
                    it.sendMessage(message)
                }
            }
            addSubcommand("events") {
                execute {
                    it.sendMessage(Events.debugTree())
                }
            }
        }
    }
}