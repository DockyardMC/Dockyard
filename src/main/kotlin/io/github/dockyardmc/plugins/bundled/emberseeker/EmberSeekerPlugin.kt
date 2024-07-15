package io.github.dockyardmc.plugins.bundled.emberseeker

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.plugins.DockyardPlugin
import io.github.dockyardmc.plugins.bundled.emberseeker.commands.EmberSeekerCommands

class EmberSeekerPlugin: DockyardPlugin {
    override val name: String = "Ember Seeker Core"
    override val author: String = "LukynkaCZE"
    override val version: String = "0.1"

    override fun load(server: DockyardServer) {
        HubBossbar()
        EmberSeekerCommands()
    }

    override fun unload(server: DockyardServer) {
        TODO("Not yet implemented")
    }
}