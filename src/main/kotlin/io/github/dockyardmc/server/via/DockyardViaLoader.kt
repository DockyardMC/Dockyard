package io.github.dockyardmc.server.via

import com.viaversion.viaversion.api.Via
import com.viaversion.viaversion.api.platform.ViaPlatformLoader
import io.github.dockyardmc.DockyardServer
import kotlin.time.Duration.Companion.seconds

class DockyardViaLoader: ViaPlatformLoader {

    override fun load() {
        DockyardServer.scheduler.runRepeatingAsync(5.seconds) {
            Via.proxyPlatform().protocolDetectorService().probeAllServers()
        }
    }

    override fun unload() {
        // no
    }
}