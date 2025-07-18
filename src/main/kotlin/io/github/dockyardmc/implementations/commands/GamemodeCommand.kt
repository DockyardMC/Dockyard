package io.github.dockyardmc.implementations.commands

import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.EnumArgument
import io.github.dockyardmc.commands.PlayerArgument
import io.github.dockyardmc.extentions.properStrictCase
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.systems.GameMode

class GamemodeCommand {

    init {
        Commands.add("/gamemode") {
            withDescription("Manages game mode of players")
            withPermission("dockyard.commands.gamemode")
            addArgument("game_mode", EnumArgument(GameMode::class))
            addOptionalArgument("player", PlayerArgument())
            execute {
                val gamemode = getEnumArgument<GameMode>("game_mode")
                val player = getArgumentOrNull<Player>("player") ?: it.getPlayerOrThrow()
                player.gameMode.value = gamemode

                val name = gamemode.name.properStrictCase()

                if(player == it.player) {
                    player.sendMessage("<gray>Set your own gamemode to <white>$name")
                } else {
                    it.sendMessage("<gray>Set gamemode of <white>$player <gray>to <white>$name")
                    player.sendMessage("<gray>Your gamemode has been updated to <white>$name")
                }
            }
        }

        Commands.add("/gmc") {
            withAliases("gms", "gma", "gmsp")
            withDescription("Sets players gamemode")
            withPermission("dockyard.commands.gamemode")

            addOptionalArgument("player", PlayerArgument())

            execute { ctx ->
                val map = mapOf(
                    "gmc" to GameMode.CREATIVE,
                    "gms" to GameMode.SURVIVAL,
                    "gmsp" to GameMode.SPECTATOR,
                    "gma" to GameMode.ADVENTURE
                )

                val command = ctx.command.removePrefix("/")
                val gamemode: GameMode = map[command]!!
                val player: Player = getArgumentOrNull<Player>("player") ?: ctx.getPlayerOrThrow()

                val name = gamemode.name.properStrictCase()

                if(player == ctx.player) {
                    player.sendMessage("<gray>Set your own gamemode to <white>$name")
                } else {
                    ctx.sendMessage("<gray>Set gamemode of <white>$player <gray>to <white>$name")
                    player.sendMessage("<gray>Your gamemode has been updated to <white>$name")
                }

                player.gameMode.value = gamemode
            }
        }
    }
}