package io.github.dockyardmc.protocol.packets.status.serverbound

import io.github.dockyardmc.protocol.Packet
import io.netty.buffer.ByteBuf

class ServerboundPingRequestPacket(val time: Long): Packet {

    override fun write(buffer: ByteBuf) {
        buffer.writeLong(time)
    }

    companion object {
        fun read(buffer: ByteBuf): ServerboundPingRequestPacket {
            return ServerboundPingRequestPacket(buffer.readLong())
        }
    }
}