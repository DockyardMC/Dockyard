package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.protocol.readOptional
import io.github.dockyardmc.utils.debug
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundCustomClickActionPacket(val id: String, val payload: String?) : ServerboundPacket {
    companion object : NetworkReadable<ServerboundCustomClickActionPacket> {
        override fun read(buffer: ByteBuf): ServerboundCustomClickActionPacket {
            return ServerboundCustomClickActionPacket(
                buffer.readString(),
                buffer.readOptional { it.readString() }
            )
        }

    }

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        debug("custom click action: $id, $payload")
    }
}