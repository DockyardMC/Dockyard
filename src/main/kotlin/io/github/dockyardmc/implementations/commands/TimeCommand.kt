package io.github.dockyardmc.implementations.commands

import io.github.dockyardmc.commands.*
import io.github.dockyardmc.extentions.toScrollText
import io.github.dockyardmc.player.Player

class TimeCommand {

    init {
        Commands.add("/time") {
            withPermission("dockyard.commands.time")

            addSubcommand("set") {
                addArgument("time", LongArgument(), simpleSuggestion("<time>"))
                execute {
                    val time = getArgument<Long>("time")
                    val player = it.getPlayerOrThrow()

                    player.world.time.value = time
                    player.sendMessage("<gray>Set time in world <white>${player.world.name}<gray> to <white>$time")
                }
            }

            addSubcommand("frozen") {
                addArgument("frozen", BooleanArgument())
                execute {
                    val frozen = getArgument<Boolean>("frozen")
                    val player = it.getPlayerOrThrow()

                    player.world.freezeTime = frozen
                    player.sendMessage("<gray>Time in world <white>${player.world.name}<gray> frozen: ${frozen.toScrollText()}")
                }
            }

            addSubcommand("player") {
                addArgument("player", PlayerArgument())
                addArgument("time", LongArgument(), simpleSuggestion("<time (-1 to reset)>"))
                execute {
                    val player = getArgument<Player>("player")
                    val time = getArgument<Long>("time")
                    player.time.value = time
                    player.sendMessage("<gray>Set client time of player <white>${player}<gray> to <white>${time}")
                }
            }
        }
    }
}