package io.github.dockyardmc.implementations.commands

import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.PlayerArgument
import io.github.dockyardmc.player.Player

class ClearCommand {

    init {
        Commands.add("/clear") {
            withPermission("dockyard.commands.clear")
            withDescription("Clears your inventory")
            addOptionalArgument("player", PlayerArgument())
            execute { ctx ->
                val player = getArgumentOrNull<Player>("player") ?: ctx.getPlayerOrThrow()
                player.inventory.clear()
            }
        }
    }
}