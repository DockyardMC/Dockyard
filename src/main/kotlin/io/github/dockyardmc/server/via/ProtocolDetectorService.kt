package io.github.dockyardmc.server.via

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import com.viaversion.viaversion.platform.AbstractProtocolDetectorService
import io.github.dockyardmc.DockyardServer

class ProtocolDetectorService: AbstractProtocolDetectorService() {

    override fun probeAllServers() {

    }

    override fun configuredServers(): MutableMap<String, Int> {
        return mutableMapOf()
    }

    override fun lowestSupportedProtocolVersion(): ProtocolVersion {
        return ProtocolVersion.getProtocol(DockyardServer.minecraftVersion.protocolId)
    }

    class ServerInfo()
}