package io.github.dockyardmc.implementations.commands

import io.github.dockyardmc.commands.BooleanArgument
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.PlayerArgument
import io.github.dockyardmc.player.Player

class ViewerCommand {

    init {
        Commands.add("/viewers") {
            withPermission("dockyard.commands.viewer")

            addSubcommand("add") {
                addArgument("entity", PlayerArgument())
                addArgument("viewer", PlayerArgument())
                execute {
                    val entity = getArgument<Player>("entity")
                    val player = getArgument<Player>("viewer")

                    entity.addViewer(player)
                }
            }

            addSubcommand("remove") {
                addArgument("entity", PlayerArgument())
                addArgument("viewer", PlayerArgument())
                addOptionalArgument("is_disconnect", BooleanArgument())
                execute {
                    val entity = getArgument<Player>("entity")
                    val player = getArgument<Player>("viewer")
                    val isDisconnect = getArgumentOrNull<Boolean>("is_disconnect") ?: false

                    entity.removeViewer(player, isDisconnect)
                }
            }
        }
    }

}