package io.github.dockyardmc.plugins.bundled.extras

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.plugins.DockyardPlugin

class DockyardExtras: DockyardPlugin {

    override var name: String = "Dockyard Extras"
    override var author: String = "LukynkaCZE"
    override var version: String = DockyardServer.versionInfo.dockyardVersion

    override fun load(server: DockyardServer) {
        JoinLeaveMessages().register()
    }

    override fun unload(server: DockyardServer) {
        TODO("Not yet implemented")
    }
}