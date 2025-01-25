package io.github.dockyardmc.protocol.packets.configuration.clientbound

import io.github.dockyardmc.protocol.Packet
import io.netty.buffer.ByteBuf

class ClientboundFinishConfigurationPacket: Packet {

    override fun write(buffer: ByteBuf) {
    }

    companion object {
        fun read(buffer: ByteBuf): ClientboundFinishConfigurationPacket {
            return ClientboundFinishConfigurationPacket()
        }
    }
}