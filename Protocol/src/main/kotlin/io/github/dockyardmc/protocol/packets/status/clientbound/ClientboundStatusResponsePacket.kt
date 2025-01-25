package io.github.dockyardmc.protocol.packets.status.clientbound

import io.github.dockyardmc.protocol.Packet
import io.github.dockyardmc.protocol.writers.readString
import io.github.dockyardmc.protocol.writers.writeString
import io.netty.buffer.ByteBuf

class ClientboundStatusResponsePacket(val statusJson: String): Packet {

    override fun write(buffer: ByteBuf) {
        buffer.writeString(statusJson)
    }

    companion object {
        fun read(buffer: ByteBuf): ClientboundStatusResponsePacket {
            return ClientboundStatusResponsePacket(buffer.readString())
        }
    }
}