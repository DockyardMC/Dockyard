package io.github.dockyardmc.plugins.bundled.particles

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.plugins.DockyardPlugin

class CoolParticles: DockyardPlugin {

    override var name: String = "Cool Particles"
    override var author: String = "LukynkaCZE"
    override var version: String = "0.1"

    override fun load(server: DockyardServer) {

        SpinningCube().register()
//        DoubleJump()

    }

    override fun unload(server: DockyardServer) {
        TODO("Not yet implemented")
    }
}