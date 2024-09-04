package io.github.dockyardmc.plugins.bundled.commands

import io.github.dockyardmc.commands.CommandException
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.EnumArgument
import io.github.dockyardmc.commands.PlayerArgument
import io.github.dockyardmc.extentions.properStrictCase
import io.github.dockyardmc.player.GameMode
import io.github.dockyardmc.player.Player
import java.lang.Exception

class GamemodeCommand {

    init {
        Commands.add("/gamemode") {
            it.description = "Changes your gamemode"
            it.permission = "dockyard.commands.creative"
            it.addArgument("mode", EnumArgument(GameMode::class))
            it.execute { executor ->
                if(!executor.isPlayer) return@execute
                val player = executor.player!!
                val gamemode = it.getEnum<GameMode>("mode")

                player.gameMode.value = gamemode
            }
        }

        Commands.add("/gmc") {
            it.aliases.add("gms")
            it.aliases.add("gma")
            it.aliases.add("gmsp")
            it.description = "Sets players gamemode"
            it.permission = "dockyard.commands.creative"

            it.addOptionalArgument("player", PlayerArgument())

            it.execute { executor ->
                val map = mapOf(
                    "gmc" to GameMode.CREATIVE,
                    "gms" to GameMode.SURVIVAL,
                    "gmsp" to GameMode.SPECTATOR,
                    "gma" to GameMode.ADVENTURE
                )

                val command = executor.command.removePrefix("/")
                val gameMode: GameMode = map[command]!!
                val target: Player = it.getOrNull<Player>("player") ?: if (executor.isPlayer) { executor.player!! } else throw CommandException("You need to specify a player target!")
                val message = "<#41cc56>Gamemode <dark_gray>| <gray>Gamemode of <yellow>$target <gray>has been set to <aqua>${gameMode.name.properStrictCase()}"

                target.gameMode.value = gameMode
                executor.sendMessage(message)
            }
        }
    }
}