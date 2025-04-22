package io.github.dockyardmc.server.via

import com.viaversion.viaversion.configuration.AbstractViaConfig
import java.io.File
import java.util.logging.Logger

class DockyardViaConfig : AbstractViaConfig(File("./via/config.yml"), Logger.getGlobal()) {

    companion object {
        val UNSUPPORTED = mutableListOf<String>()
        init {
            UNSUPPORTED.addAll(BUKKIT_ONLY_OPTIONS)
            UNSUPPORTED.addAll(VELOCITY_ONLY_OPTIONS)
        }
    }

    override fun handleConfig(config: MutableMap<String, Any>) {

    }

    override fun getUnsupportedOptions(): MutableList<String> {
        return UNSUPPORTED
    }
}