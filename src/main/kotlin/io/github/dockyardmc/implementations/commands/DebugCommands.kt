package io.github.dockyardmc.implementations.commands

import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.WorldArgument
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.world.World

class DebugCommands {

    init {
        Commands.add("debugworld") {
            addArgument("world", WorldArgument())
            execute {
                val world = getArgument<World>("world")
                val message = buildString {
                    append("\n")
                    appendLine("<lime>Chunks in memory: <aqua>${world.chunks.size}")
                    appendLine("<lime>Custom data blocks: <aqua>${world.customDataBlocks.size}")
                    appendLine("<lime>Event pool size: <aqua>${world.eventPool.eventList().size}")
                }
                it.sendMessage(message)
            }
        }

        Commands.add("debugevents") {
            execute {
                it.sendMessage(Events.debugTree())
            }
        }
    }
}