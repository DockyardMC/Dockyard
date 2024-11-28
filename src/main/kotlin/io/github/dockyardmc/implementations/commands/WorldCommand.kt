package io.github.dockyardmc.implementations.commands

import io.github.dockyardmc.commands.*
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.DimensionTypes
import io.github.dockyardmc.world.WorldManager
import io.github.dockyardmc.world.generators.FlatWorldGenerator
import kotlin.time.Duration.Companion.milliseconds

class WorldCommand {

    init {

        fun suggestWorlds(player: Player): Collection<String> = WorldManager.worlds.keys.toList()

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

            addSubcommand("create") {
                addArgument("world", StringArgument(), simpleSuggestion("<name>"))
                execute {
                    val worldName = getArgument<String>("world")
                    WorldManager.create(worldName, FlatWorldGenerator(), DimensionTypes.OVERWORLD)
                    it.sendMessage("<lime>Created world <yellow>$worldName")
                }
            }
        }
    }
}