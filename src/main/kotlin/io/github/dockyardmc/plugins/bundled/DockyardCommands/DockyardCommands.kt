package io.github.dockyardmc.plugins.bundled.DockyardCommands

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.EnumArgument
import io.github.dockyardmc.commands.PlayerArgument
import io.github.dockyardmc.commands.StringArgument
import io.github.dockyardmc.player.GameMode
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.SkinManager
import io.github.dockyardmc.plugins.DockyardPlugin
import io.github.dockyardmc.plugins.PluginManager.loadedPlugins

class DockyardCommands: DockyardPlugin {

    override var name: String = "DockyardCommands"
    override var version: String = DockyardServer.versionInfo.dockyardVersion
    override var author: String = "LukynkaCZE"

    override fun load(server: DockyardServer) {
        Commands.add("version") {
            it.description = "Sends the server version"
            it.aliases.add("ver")
            it.aliases.add("info")
            it.aliases.add("server")
            it.aliases.add("dockyard")
            it.execute { executor ->
                executor.sendMessage("<aqua>DockyardMC <dark_gray>| <gray>This server is running <yellow>DockyardMC ${DockyardServer.versionInfo.dockyardVersion}<gray>. A custom Minecraft server implementation in Kotlin. <aqua><hover|'<aqua>https://github.com/DockyardMC/Dockyard'><click|open_url|https://github.com/DockyardMC/Dockyard>[Github]<reset>")
            }
        }

        Commands.add("/gamemode") {
            it.description = "Changes your gamemode"
            it.addArgument("mode", EnumArgument(GameMode::class))
            it.execute { executor ->
                if(!executor.isPlayer) return@execute
                val player = executor.player!!
                val gamemode = it.getEnum<GameMode>("mode")

                player.gameMode.value = gamemode
            }
        }

        Commands.add("/skin") {
            it.description = "Sets/Updates skin of a player"
            it.addArgument("player", PlayerArgument())
            it.addArgument("action", StringArgument(mutableListOf("update", "set")))
            it.addOptionalArgument("new skin", StringArgument())

            it.execute { executor ->
                val player = it.get<Player>("player")
                val action = it.get<String>("action")
                val newSkin = it.getOrNull<String>("new skin")

                var message = ""
                when(action) {

                    "update" -> {
                        SkinManager.setSkinOf(player, player.uuid)
                        message = "Updated skin of <yellow>$player<gray>!"
                    }
                    "set" -> {
                        if(newSkin == null) throw Exception("New skin must be specified!")
                        SkinManager.setSkinOf(player, newSkin)
                        message = "Set skin of <yellow>$player<gray> to <aqua>$newSkin!"
                    }
                    else -> throw Exception("Invalid action, only available are update/set")
                }
                executor.sendMessage("<#f54295>Skins <dark_gray>| <gray>$message")
            }
        }

        Commands.add("/help") {
            it.description = "Shows list of commands"
            it.aliases.add("commands")
            it.execute { executor ->
                val accessibleCommands = Commands.commands.filter { command -> !command.value.isAlias && executor.hasPermission(command.value.permission) }

                val message = buildString {
                    val commandSize = when (accessibleCommands.size) {
                        0 -> "are <red>no commands</red>"
                        1 -> "is <lime>1 command</lime>"
                        else -> "are <lime>${accessibleCommands.size} commands</lime>"
                    }

                    appendLine(" ")
                    appendLine("<aqua>DockyardMC <dark_gray>| <gray>There $commandSize<gray> loaded:")

                    accessibleCommands.forEach { command ->
                        val description = command.value.description.ifEmpty { "<dark_gray><italics>No Description" }
                        appendLine("<gray>  - <yellow>/${command.key} <gray> - <gray>$description")
                    }
                }
                executor.sendMessage(message)
            }
        }

        Commands.add("/plugins") {
            it.description = "Shows list of plugins"
            it.aliases.add("pl")
            it.aliases.add("plugin")
            it.aliases.add("extensions")

            it.execute { executor ->
                val pluginsSize = when(loadedPlugins.size) {
                    0 -> "are <red>no plugins</red>"
                    1 -> "is <lime>1 plugin</lime>"
                    else -> "are <lime>${loadedPlugins.size} plugins</lime>"
                }
                val message = buildString {
                    appendLine()
                    appendLine("<aqua>DockyardMC <dark_gray>| <gray>There $pluginsSize<gray> loaded:")
                    loadedPlugins.forEach { plugin ->
                        appendLine("<gray>  - <yellow>${plugin.name} <gray>version <orange>${plugin.version}<gray> by <aqua>${plugin.author}")
                    }
                }
                executor.sendMessage(message)
            }
        }
    }

    override fun unload(server: DockyardServer) {
        TODO("Not yet implemented")
    }
}