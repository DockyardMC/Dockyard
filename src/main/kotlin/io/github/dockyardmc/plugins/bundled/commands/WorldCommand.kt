package io.github.dockyardmc.plugins.bundled.commands

import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.EnumArgument
import io.github.dockyardmc.commands.StringArgument
import io.github.dockyardmc.commands.WorldArgument
import io.github.dockyardmc.registry.DimensionTypes
import io.github.dockyardmc.world.WorldManager
import io.github.dockyardmc.world.generators.FlatWorldGenerator

class WorldCommand {

    init {
        Commands.add("/world") {
            it.permission = "dockyard.commands.world"
            it.aliases.add("worlds")
            it.addArgument("action", EnumArgument(Action::class))
            it.addOptionalArgument("world", WorldArgument())
            it.execute { ctx ->
                val player = ctx.playerOrThrow()
                val arg = it.getEnum<Action>("action")
                val worldName = it.getOrNull<String>("world")

                when(arg) {
                    Action.TP -> {
                        if(worldName == null) throw Exception("argument world is required for this command!")
                        val world = WorldManager.worlds[worldName] ?: throw Exception("World with name <orange>$worldName<red> was not found!")
                        player.teleport(world.defaultSpawnLocation)
                    }
                    Action.LIST -> {
                        val message = buildString {
                            appendLine()
                            WorldManager.worlds.forEach { world ->
                                appendLine("<gray>  - <yellow>${world.key} <gray>type: <lime>${world.value.generator::class.simpleName}<gray>, <orange>${world.value.dimensionType.identifier.replace("minecraft:", "")}<gray>, age: <aqua>${world.value.worldAge}")
                            }
                        }
                        player.sendMessage(message)
                    }
                    Action.REMOVE -> {
                        if(worldName == null) throw Exception("argument world is required for this command!")
                        val world = WorldManager.worlds[worldName] ?: throw Exception("World with name <orange>$worldName<red> was not found!")
                        if(world.name == "main") throw Exception("You cannot remove the main world")
                        WorldManager.delete(world)
                    }
                    Action.CREATE,
                    Action.ADD -> {
                        if(worldName == null) throw Exception("argument world is required for this command!")
                        if(WorldManager.worlds[worldName] != null) throw Exception("World with name <orange>$worldName<red> already exists!")
                        val world = WorldManager.create(worldName, FlatWorldGenerator(), DimensionTypes.OVERWORLD)
                        world.canBeJoined.valueChanged { ready ->
                            if(ready.newValue) player.teleport(world.defaultSpawnLocation)
                        }
                    }
                }
            }
        }
    }

    enum class Action {
        TP,
        LIST,
        REMOVE,
        ADD,
        CREATE
    }
}