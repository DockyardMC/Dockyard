package io.github.dockyardmc.implementations.commands

import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.StringArgument
import io.github.dockyardmc.commands.SuggestionProvider
import io.github.dockyardmc.config.ConfigManager
import io.github.dockyardmc.schematics.SchematicReader
import io.github.dockyardmc.schematics.placeSchematic
import java.io.File

class SchematicCommand {
    init {
        Commands.add("/schematic") {
            withPermission("dockyard.commands.schematic")

            addSubcommand("place") {
                addArgument("path", StringArgument(), SuggestionProvider.simple("<path to schematic file>"))
                execute {
                    val player = it.getPlayerOrThrow()
                    val path = getArgument<String>("path")
                    val start = System.currentTimeMillis()
                    player.world.placeSchematic {
                        schematic = SchematicReader.read(File(path))
                        location = player.location
                        then = {
                            val end = System.currentTimeMillis()
                            player.sendMessage("<gray>Schematic pasted in <lime>${end - start}ms")
                        }
                    }
                }
            }

            if(ConfigManager.config.implementationConfig.cacheSchematics) {
                addSubcommand("cache") {
                    execute {
                        val message = buildString {
                            append("<gray>Current schematic cache:\n")
                            SchematicReader.cache.forEach { schem ->
                                appendLine("<gray>- <lime>${schem.key.subSequence(0, 16)} <dark_gray>- <aqua>(${schem.value.blocks.size} bytes, ${schem.value.pallete.values.size} blocks in pellet)")
                            }
                        }
                        it.sendMessage(message)
                    }
                }
            }
        }
    }
}