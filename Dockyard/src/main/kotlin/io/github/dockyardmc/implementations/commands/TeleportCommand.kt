package io.github.dockyardmc.implementations.commands

import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.PlayerArgument
import io.github.dockyardmc.player.Player

class TeleportCommand {

    init {
        Commands.add("/tp") {
            withDescription("Lets you teleport players to other players")
            withPermission("dockyard.commands.teleport")
            withAliases("teleport")
            addArgument("first", PlayerArgument())
            addOptionalArgument("second", PlayerArgument())
            execute {
                val second = getArgumentOrNull<Player>("second")
                if(second == null) {
                    val player = it.getPlayerOrThrow()
                    val target = getArgument<Player>("first")
                    player.teleport(target.location)
                    player.sendMessage("<gray>Teleported to <white>$target")
                } else {
                    val player = getArgument<Player>("first")
                    val target = getArgument<Player>("second")
                    player.teleport(target.location)
                    target.sendMessage("<gray>Teleported <white>$player <gray>to <white>$target")
                    player.sendMessage("<gray>You have been teleported to <white>$player")
                }
            }
        }
    }
}