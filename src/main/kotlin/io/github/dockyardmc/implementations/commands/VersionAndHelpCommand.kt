package io.github.dockyardmc.implementations.commands

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.utils.DockyardBranding

class VersionAndHelpCommand {

    init {
        Commands.add("version") {
            withDescription("Sends the server version")
            withAliases("ver", "info", "server", "dockyard")
            execute {
                it.sendMessage("")
                it.sendMessage("<gray>This server is running <${DockyardBranding.COLOR}>DockyardMC ${DockyardServer.versionInfo.dockyardVersion}<gray>. A custom Minecraft server implementation in Kotlin. <yellow><hover:show_text:'<yellow>https://github.com/DockyardMC/Dockyard'><click:open_url:https://github.com/DockyardMC/Dockyard>[Github]<reset>")
                it.sendMessage("")
            }
        }

        Commands.add("/help") {
            withDescription("Shows list of commands")
            withAliases("commands")
            execute { ctx ->
                val accessibleCommands = Commands.commands.filter { command -> !command.value.isAlias && ctx.hasPermission(command.value.permission) }

                val message = buildString {
                    val commandSize = when (accessibleCommands.size) {
                        0 -> "are <red>no commands</red>"
                        1 -> "is <lime>1 command</lime>"
                        else -> "are <lime>${accessibleCommands.size} commands</lime>"
                    }

                    appendLine(" ")
                    appendLine("<gray>There $commandSize<gray> loaded:")

                    accessibleCommands.forEach { command ->
                        val description = command.value.description.ifEmpty { "<dark_gray><italics>No Description" }
                        appendLine("<gray>  - <yellow>/${command.key} <gray> - <gray>$description")
                    }
                }
                ctx.sendMessage(message)
            }
        }
    }
}