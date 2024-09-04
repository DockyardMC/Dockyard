package io.github.dockyardmc.plugins.bundled.commands

import io.github.dockyardmc.commands.*
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.DimensionTypes
import io.github.dockyardmc.world.WorldManager
import io.github.dockyardmc.world.generators.FlatWorldGenerator

class WorldCommand {

    init {

        fun suggestWorlds(): CommandSuggestions = SuggestionProvider.withContext { WorldManager.worlds.keys.toList() }

        val command = Commands.subcommandBase("/world")
        command.permission = "dockyard.commands.world"

        command.addSubcommand("tp") {
            it.addArgument("world", StringArgument(), suggestWorlds())
            it.addOptionalArgument("player", PlayerArgument())
            it.execute { ctx ->
                val world = WorldManager.worlds[it.get<String>("world")] ?: throw CommandException("World with that name not found")
                val player = it.getOrNull<Player>("player") ?: ctx.playerOrThrow()
                player.teleport(world.defaultSpawnLocation)
            }
        }

        command.addSubcommand("list") {
            it.execute { ctx ->
                val message = buildString {
                    appendLine()
                    WorldManager.worlds.forEach { world ->
                        appendLine("<gray>  - <yellow>${world.key} <gray>type: <lime>${world.value.generator::class.simpleName}<gray>, <orange>${world.value.dimensionType.identifier.replace("minecraft:", "")}<gray>, age: <aqua>${world.value.worldAge}")
                    }
                }
                ctx.sendMessage(message)
            }
        }

        command.addSubcommand("delete") {
            it.addArgument("world", StringArgument(), suggestWorlds())
            it.execute { ctx ->
                val world = WorldManager.worlds[it.get<String>("world")] ?: throw CommandException("World with that name not found")
                WorldManager.delete(world)
                ctx.sendMessage("<red>Deleted world <yellow>${world.name}")
            }
        }

        command.addSubcommand("create") {
            it.addArgument("world", StringArgument(), SuggestionProvider.simple("<name>"))
            it.execute { ctx ->
                val worldName = it.get<String>("world")
                WorldManager.create(worldName, FlatWorldGenerator(), DimensionTypes.OVERWORLD)
                ctx.sendMessage("<lime>Created world <yellow>$worldName")
            }
        }
    }
}