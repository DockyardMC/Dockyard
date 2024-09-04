package io.github.dockyardmc.plugins.bundled.commands

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.commands.Commands

class VersionAndHelpCommand {

    init {
        Commands.add("version") {
            withDescription("Sends the server version")
            withAliases("ver", "info", "server", "dockyard")
            execute {
                it.sendMessage("<aqua>DockyardMC <dark_gray>| <gray>This server is running <yellow>DockyardMC ${DockyardServer.versionInfo.dockyardVersion}<gray>. A custom Minecraft server implementation in Kotlin. <aqua><hover|'<aqua>https://github.com/DockyardMC/Dockyard'><click|open_url|https://github.com/DockyardMC/Dockyard>[Github]<reset>")
            }
        }

        Commands.add("/help") {
            withDescription("Shows list of commands")
            withAliases("commands")
            execute {
                val accessibleCommands = Commands.commands.filter { command -> !command.value.isAlias && it.hasPermission(command.value.permission) }

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
                it.sendMessage(message)
            }
        }
    }
}