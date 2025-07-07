package io.github.dockyardmc.implementations.commands

import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.PlayerArgument
import io.github.dockyardmc.commands.StringArgument
import io.github.dockyardmc.commands.simpleSuggestion
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.SkinManager

class SkinCommand {
    init {
        Commands.add("/skin") {
            withDescription("Manages skins of players")
            withPermission("dockyard.commands.skin")

            addSubcommand("set") {
                addArgument("player", PlayerArgument())
                addArgument("skin", StringArgument(), simpleSuggestion("<player username>"))
                execute { ctx ->
                    val player = getArgument<Player>("player")
                    val skin = getArgument<String>("skin")
                    SkinManager.setSkinFromUsername(player, skin).thenAccept { success ->
                        val message = if (success) "Successfully set your skin to skin of <white>$skin" else "<red>There was an error while setting your skin!"
                        ctx.sendMessage(message)
                    }
                }
            }
        }
    }
}