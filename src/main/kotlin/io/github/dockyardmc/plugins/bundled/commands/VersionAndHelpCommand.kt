package io.github.dockyardmc.plugins.bundled.commands

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.commands.Commands

class VersionAndHelpCommand {

    init {
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
    }
}