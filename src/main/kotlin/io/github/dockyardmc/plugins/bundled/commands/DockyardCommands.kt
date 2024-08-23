package io.github.dockyardmc.plugins.bundled.commands

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.plugins.DockyardPlugin

class DockyardCommands: DockyardPlugin {

    override var name: String = "Dockyard Commands"
    override var version: String = DockyardServer.versionInfo.dockyardVersion
    override var author: String = "LukynkaCZE"

    override fun load(server: DockyardServer) {

        PluginsCommand()
        GamemodeCommand()
        VersionAndHelpCommand()
        WorldCommand()

    }

    override fun unload(server: DockyardServer) {
        TODO("Not yet implemented")
    }
}