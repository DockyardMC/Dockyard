package io.github.dockyardmc.implementations.commands

import io.github.dockyardmc.commands.BooleanArgument
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.LongArgument
import io.github.dockyardmc.extentions.toScrollText

class TimeCommand {

    init {
        Commands.add("/time") {
            withPermission("dockyard.commands.time")

            addSubcommand("set") {
                addArgument("time", LongArgument())
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
        }
    }
}