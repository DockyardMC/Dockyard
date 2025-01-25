package io.github.dockyardmc.protocol.packets.login.clientbound

import io.github.dockyardmc.protocol.Packet
import io.github.dockyardmc.protocol.writers.readVarInt
import io.github.dockyardmc.protocol.writers.writeVarInt
import io.netty.buffer.ByteBuf

class ClientboundSetCompressionPacket(val compressionThreshold: Int): Packet {

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(compressionThreshold)
    }

    companion object {
        fun read(buffer: ByteBuf): ClientboundSetCompressionPacket {
            return ClientboundSetCompressionPacket(buffer.readVarInt())
        }
    }
}