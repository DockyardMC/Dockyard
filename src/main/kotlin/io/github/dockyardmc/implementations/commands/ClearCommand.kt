package io.github.dockyardmc.implementations.commands

import io.github.dockyardmc.commands.Commands

class ClearCommand {

    init {
        Commands.add("/clear") {
            withPermission("dockyard.commands.clear")
            withDescription("Clears your inventory")
            execute {
                val player = it.getPlayerOrThrow()
                player.inventory.clear()
            }
        }
    }
}