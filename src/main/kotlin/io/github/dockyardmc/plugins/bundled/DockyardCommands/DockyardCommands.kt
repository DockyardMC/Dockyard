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

        SkinCommand()
        PluginsCommand()
        GamemodeCommand()
        VersionAndHelpCommand()

    }

    override fun unload(server: DockyardServer) {
        TODO("Not yet implemented")
    }
}