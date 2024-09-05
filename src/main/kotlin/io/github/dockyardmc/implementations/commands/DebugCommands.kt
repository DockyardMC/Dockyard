package io.github.dockyardmc.implementations.commands

import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.WorldArgument
import io.github.dockyardmc.world.World

class DebugCommands {

    init {
        Commands.add("chunks") {
            addArgument("world", WorldArgument())
            execute {
                val player = it.getPlayerOrThrow()
                val world = getArgument<World>("world")
                val message = buildString {
                    appendLine("<lime>Chunks in memory: <aqua>${world.chunks.size}")
                    appendLine("<lime>Surface: <aqua>${player.location.getChunk()!!.worldSurface}")
                    appendLine("<lime>Motion blocking: <aqua>${player.location.getChunk()!!.motionBlocking}")
                    appendLine("<lime>Index: <aqua>${player.location.getChunk()!!.getIndex()}")
                }
                it.sendMessage(message)
            }
        }
    }
}