package io.github.dockyardmc.protocol.packets.login

import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.protocol.readOptional
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundLoginPluginResponsePacket(val messageId: Int, val data: ByteBuf? = null) : ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.loginPluginMessageHandler.handleResponse(messageId, data)
    }

    companion object : NetworkReadable<ServerboundLoginPluginResponsePacket> {
        override fun read(buffer: ByteBuf): ServerboundLoginPluginResponsePacket {
            return ServerboundLoginPluginResponsePacket(buffer.readVarInt(), buffer.readOptional { b -> b.readBytes(b.readableBytes()) })
        }
    }
}