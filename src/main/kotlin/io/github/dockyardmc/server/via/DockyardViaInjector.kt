package io.github.dockyardmc.server.via

import com.viaversion.viaversion.api.platform.ViaInjector
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import com.viaversion.viaversion.connection.UserConnectionImpl
import com.viaversion.viaversion.libs.gson.JsonObject
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl
import cz.lukynka.prettylog.log
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.protocol.ChannelHandlers
import io.netty.channel.Channel

class DockyardViaInjector : ViaInjector {

    companion object {
        fun injectPipeline(channel: Channel, clientside: Boolean) {
            val connection = UserConnectionImpl(channel, clientside)
            val protocolPipelineImpl = ProtocolPipelineImpl(connection)

            val encoder = ViaEncodeHandler(connection)
            val decoder = ViaDecodeHandler(connection)

            channel.pipeline().addBefore(ChannelHandlers.RAW_PACKET_ENCODER, ChannelHandlers.VIA_ENCODER, encoder)
            channel.pipeline().addBefore(ChannelHandlers.RAW_PACKET_DECODER, ChannelHandlers.VIA_DECODER, decoder)

            connection.isActive = true
            log("Injected via version into pipeline", DockyardViaVersionPlatform.LOG_TYPE)
        }
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