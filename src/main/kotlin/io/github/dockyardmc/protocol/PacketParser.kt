package io.github.dockyardmc.protocol

import io.netty.buffer.ByteBuf
import io.github.dockyardmc.protocol.packets.status.ServerboundHandshakePacket
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.protocol.packets.status.ServerboundPingRequestPacket

object PacketParser {

    fun parsePacket(id: Int, buffer: ByteBuf): ServerboundPacket? {
        return when (id) {
            0 -> ServerboundHandshakePacket.read(buffer)
            1 -> ServerboundPingRequestPacket.read(buffer)
            else -> null
        }
    }
}