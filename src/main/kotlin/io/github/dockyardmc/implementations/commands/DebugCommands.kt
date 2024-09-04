package io.github.dockyardmc.implementations.commands

import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.WorldArgument
import io.github.dockyardmc.world.World

class DebugCommands {

    init {
        Commands.add("chunks") {
            addArgument("world", WorldArgument())
            execute {
                val world = getArgument<World>("world")
                val message = buildString {
                    appendLine("<lime>Chunks in memory: <aqua>${world.chunks.size}")
                }
                it.sendMessage(message)
            }
        }
    }
}