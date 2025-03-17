package io.github.dockyardmc.implementations.commands

import io.github.dockyardmc.commands.CommandException
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.PlayerArgument
import io.github.dockyardmc.commands.StringArgument
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.world.WorldManager

class WorldCommand {

    companion object {
        fun suggestWorlds(player: Player): Collection<String> = WorldManager.worlds.keys.toList()
    }

    init {

        Commands.add("/world") {
            withPermission("dockyard.commands.world")
            withDescription("Command for managing worlds (creation, tp, deletion etc.)")

            addSubcommand("tp") {
                addArgument("world", StringArgument(), ::suggestWorlds)
                addOptionalArgument("player", PlayerArgument())
                execute { ctx ->
                    val world = WorldManager.worlds[getArgument<String>("world")] ?: throw CommandException("World with that name not found")
                    val player = getArgumentOrNull<Player>("player") ?: ctx.getPlayerOrThrow()
                    player.teleport(world.defaultSpawnLocation)
                }
            }

            addSubcommand("list") {
                execute {
                    val message = buildString {
                        appendLine()
                        WorldManager.worlds.forEach { world ->
                            appendLine("<gray>  - <yellow>${world.key} <gray>type: <lime>${world.value.generator::class.simpleName}<gray>, <orange>${world.value.dimensionType::class.simpleName}<gray>, age: <aqua>${world.value.worldAge}")
                        }
                    }
                    it.sendMessage(message)
                }
            }

            addSubcommand("delete") {
                addArgument("world", StringArgument(), ::suggestWorlds)
                execute {
                    val world = WorldManager.worlds[getArgument<String>("world")] ?: throw CommandException("World with that name not found")
                    WorldManager.delete(world)
                    it.sendMessage("<red>Deleted world <yellow>${world.name}")
                }
            }
        }
    }
}