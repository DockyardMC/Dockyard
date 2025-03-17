package io.github.dockyardmc.implementations.commands

import io.github.dockyardmc.commands.*
import io.github.dockyardmc.extentions.toScrollText
import io.github.dockyardmc.player.Player

class TimeCommand {

    private companion object {
        val TIMES_OF_DAY: Map<String, Long> = mapOf(
            "day" to 1000,
            "midnight" to 18000,
            "night" to 13000,
            "noon" to 6000
        )

        fun suggestTime(player: Player): List<String> {
            return TIMES_OF_DAY.keys.toList()
        }
    }

    init {
        Commands.add("/time") {
            withDescription("Lets you change and freeze time of the world")
            withPermission("dockyard.commands.time")

            addSubcommand("set") {
                addArgument("time", StringArgument(), ::suggestTime)
                execute { ctx ->
                    val time = getArgument<String>("time")
                    val player = ctx.getPlayerOrThrow()
                    var newTime: Long? = null

                    if (TIMES_OF_DAY.contains(time)) newTime = TIMES_OF_DAY[time]!!

                    if (newTime == null) {
                        val tryParse = time.toLongOrNull() ?: throw CommandException("$time is not of type Long, nor does it represent 'day', 'midnight', 'night', or 'noon'")
                        newTime = tryParse
                    }

                    player.world.time.value = newTime
                    player.sendMessage("<gray>Set time in world <white>${player.world.name}<gray> to <white>$newTime")
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