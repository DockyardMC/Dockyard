package io.github.dockyardmc.protocol.packets.status.clientbound

import io.github.dockyardmc.protocol.Packet
import io.netty.buffer.ByteBuf

class ClientboundPingResponsePacket(val time: Long): Packet {

    override fun write(buffer: ByteBuf) {
        buffer.writeLong(time)
    }

    companion object {
        fun read(buffer: ByteBuf): ClientboundPingResponsePacket {
            return ClientboundPingResponsePacket(buffer.readLong())
        }
    }

}