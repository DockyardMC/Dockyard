package io.github.dockyardmc.plugins.bundled

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.EnumArgument
import io.github.dockyardmc.player.GameMode
import io.github.dockyardmc.plugins.DockyardPlugin
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundPlayerGameEventPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.GameEvent

class DockyardCommands: DockyardPlugin {

    override var name: String = "DockyardCommands"
    override var version: String = DockyardServer.versionInfo.dockyardVersion
    override var author: String = "LukynkaCZE"

    override fun load(server: DockyardServer) {
        Commands.add("version") {
            it.aliases.add("ver")
            it.aliases.add("info")
            it.aliases.add("server")
            it.aliases.add("dockyard")
            it.internalExecutorDoNotUse = { executor ->
                val message =
                    "<aqua>DockyardMC <dark_gray>| <gray>This server is running <yellow>DockyardMC ${DockyardServer.versionInfo.dockyardVersion}<gray>. A custom Minecraft server implementation in Kotlin. <aqua><hover|'<aqua>https://github.com/DockyardMC/Dockyard'><click|open_url|https://github.com/DockyardMC/Dockyard>[Github]<reset>"
                if (executor.isPlayer) {
                    executor.player!!.sendMessage(message)
                } else {
                    executor.console!!.sendMessage(message)
                }
            }
        }

        Commands.add("/gamemode") {
            it.addArgument("gamemode", EnumArgument(GameMode::class))
            it.execute { executor ->
                if(!executor.isPlayer) return@execute
                val player = executor.player!!
                val gamemode = it.getEnum<GameMode>("gamemode")

                player.gameMode.value = gamemode
            }
        }
    }

    override fun unload(server: DockyardServer) {
        TODO("Not yet implemented")
    }

}