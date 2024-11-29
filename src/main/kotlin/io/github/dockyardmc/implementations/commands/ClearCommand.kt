package io.github.dockyardmc.implementations.commands

import io.github.dockyardmc.commands.Commands

class ClearCommand {

    init {
        Commands.add("/clear") {
            withPermission("dockyard.commands.clear")
            execute {
                val player = it.getPlayerOrThrow()
                player.inventory.clear()
            }
        }
    }
}