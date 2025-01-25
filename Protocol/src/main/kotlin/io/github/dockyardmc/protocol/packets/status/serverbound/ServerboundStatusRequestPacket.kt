package io.github.dockyardmc.protocol.packets.status.serverbound

import io.github.dockyardmc.protocol.Packet
import io.netty.buffer.ByteBuf

class ServerboundStatusRequestPacket: Packet {

    override fun write(buffer: ByteBuf) {
    }

    companion object {
        fun read(buffer: ByteBuf): ServerboundStatusRequestPacket {
            return ServerboundStatusRequestPacket()
        }
    }
}