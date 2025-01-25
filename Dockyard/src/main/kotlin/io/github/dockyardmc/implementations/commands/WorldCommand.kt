package io.github.dockyardmc.implementations.commands

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.commands.*
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.DimensionTypes
import io.github.dockyardmc.world.WorldManager
import io.github.dockyardmc.world.generators.FlatWorldGenerator

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
                execute { ctx ->
                    val worldName = getArgument<String>("world")
                    WorldManager.createWithFuture(worldName, FlatWorldGenerator(), DimensionTypes.OVERWORLD).thenAccept { world ->
                        ctx.sendMessage("<lime>Created world <yellow>${world.name}")
                        world.defaultSpawnLocation = Location(0, 201, 0, world)
                        DockyardServer.broadcastMessage("")
                    }
                }
            }
        }
    }
}