package io.github.dockyardmc.server.via

import com.viaversion.viaversion.api.platform.ViaInjector
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import com.viaversion.viaversion.connection.UserConnectionImpl
import com.viaversion.viaversion.libs.gson.JsonObject
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl
import io.github.dockyardmc.DockyardServer
import io.netty.channel.Channel

class DockyardViaInjector : ViaInjector {

    private fun injectPipeline(channel: Channel, clientside: Boolean) {
        val connection = UserConnectionImpl(channel, clientside)
        val protocolPipelineImpl = ProtocolPipelineImpl(connection)


    }

    override fun inject() {
    }

    override fun uninject() {
        // no
    }

    override fun getServerProtocolVersion(): ProtocolVersion {
        return ProtocolVersion.getProtocol(DockyardServer.minecraftVersion.protocolId)
    }

    override fun getDump(): JsonObject {
        return JsonObject() //wtf is this
    }
}