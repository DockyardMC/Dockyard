package io.github.dockyardmc.plugins.bundled.DockyardCommands

import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.PlayerArgument
import io.github.dockyardmc.commands.StringArgument
import io.github.dockyardmc.player.Player

class SkinCommand {

    init {

        Commands.add("/skin") {
            it.description = "Sets/Updates skin of a player"
            it.addArgument("player", PlayerArgument())
            it.addArgument("action", StringArgument(mutableListOf("update", "set")))
            it.addOptionalArgument("new skin", StringArgument())

            it.execute { executor ->
                val player = it.get<Player>("player")
                val action = it.get<String>("action")
                val newSkin = it.getOrNull<String>("new skin")

                val message: String
                when(action) {

                    "update" -> {
                        player.updateSkin()
                        message = "Updated skin of <yellow>$player<gray>!"
                    }
                    "set" -> {
                        if(newSkin == null) throw Exception("New skin must be specified!")
                        player.setSkin(newSkin)
                        message = "Set skin of <yellow>$player<gray> to <aqua>$newSkin!"
                    }
                    else -> throw Exception("Invalid action, only available are update/set")
                }
                executor.sendMessage("<#f54295>Skins <dark_gray>| <gray>$message")
            }
        }
    }
}