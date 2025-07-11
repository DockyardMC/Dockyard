package io.github.dockyardmc.implementations.commands

import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.IntArgument
import io.github.dockyardmc.commands.WorldArgument
import io.github.dockyardmc.extentions.toScrollText
import io.github.dockyardmc.world.World
import kotlin.time.Duration.Companion.milliseconds

class SchedulerCommand {

    init {
        Commands.add("/scheduler") {
            withPermission("dockyard.commands.scheduler")
            withDescription("Manages tickrate and state of world scheduler")

            addSubcommand("tickrate") {
                addArgument("world", WorldArgument())
                addArgument("rate", IntArgument())
                execute {
                    val world = getArgument<World>("world")
                    val rate = getArgument<Int>("rate")

                    world.scheduler.tickRate.value = rate.milliseconds
                    it.sendMessage("<gray>Set tick rate of world <white>${world.name} <gray>to <yellow>${rate}ms")
                }
            }

            addSubcommand("reset") {
                addArgument("world", WorldArgument())
                execute {
                    val world = getArgument<World>("world")
                    world.scheduler.syncWithGlobalScheduler()
                    it.sendMessage("<gray>Synchronized scheduler of world <white>${world.name} <gray>to the global scheduler")
                }
            }

            addSubcommand("pause") {
                addArgument("world", WorldArgument())
                execute {
                    val world = getArgument<World>("world")
                    world.scheduler.pause()

                    it.sendMessage("<gray>Set running state of scheduler in world <white>${world.name} <gray>to ${(!world.scheduler.paused.value).toScrollText()}")
                }
            }

            addSubcommand("resume") {
                addArgument("world", WorldArgument())
                execute {
                    val world = getArgument<World>("world")
                    world.scheduler.resume()

                    it.sendMessage("<gray>Set running state of scheduler in world <white>${world.name} <gray>to ${(!world.scheduler.paused.value).toScrollText()}")
                }
            }
        }
    }
}