package io.github.dockyardmc.plugins.bundled.DockyardExtras

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.plugins.DockyardPlugin
import io.github.dockyardmc.plugins.bundled.DockyardExtras.items.SpawnEggImplementation

class DockyardExtras: DockyardPlugin {

    override var name: String = "Dockyard Extras"
    override var author: String = "LukynkaCZE"
    override var version: String = DockyardServer.versionInfo.dockyardVersion

    override fun load(server: DockyardServer) {

        SpawnEggImplementation()
        JoinLeaveMessages().register()

    }

    override fun unload(server: DockyardServer) {
        TODO("Not yet implemented")
    }
}