package io.github.dockyardmc.implementations.commands

import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.player.PlayerManager

class ListCommand {

    init {
        Commands.add("/list") {
            execute { ctx ->
                val size = PlayerManager.players.size
                if(size == 0) {
                    ctx.sendMessage(" ")
                    ctx.sendMessage(" <gray>There are <red>no players<gray> online")
                    ctx.sendMessage(" ")
                    return@execute
                }

                val message = buildString {
                    val playerWord = if(size == 1) "player" else "players"
                    val isAreWord = if(size == 1) "is" else "are"
                    appendLine(" ")
                    appendLine("<gray>There $isAreWord <lime>${PlayerManager.players.size}<gray> $playerWord online:")
                    PlayerManager.players.forEachIndexed { index, player ->
                        if(index % 5 == 0 && index != 0) append("\n")
                        append("<yellow>${player.username}<gray>")
                        if(index != (size - 1)) append(", ")
                    }
                    appendLine(" ")
                }

                ctx.sendMessage(message)
            }
        }
    }
}