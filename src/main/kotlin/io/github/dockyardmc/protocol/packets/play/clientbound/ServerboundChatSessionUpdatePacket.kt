package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.readByteArray
import io.github.dockyardmc.extentions.readInstant
import io.github.dockyardmc.extentions.readUUID
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.cryptography.PlayerSession
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundChatSessionUpdatePacket(
    val playerSession: PlayerSession
) : ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        // dockyard does not support encrypted chat
    }

    companion object {
        fun read(buffer: ByteBuf): ServerboundChatSessionUpdatePacket {
            return ServerboundChatSessionUpdatePacket(PlayerSession(
                buffer.readUUID(),
                buffer.readInstant(),
                buffer.readByteArray(),
                buffer.readByteArray()
            ))
        }
    }
}
