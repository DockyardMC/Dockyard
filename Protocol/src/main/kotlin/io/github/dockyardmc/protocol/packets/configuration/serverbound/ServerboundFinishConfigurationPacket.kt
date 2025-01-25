package io.github.dockyardmc.protocol.packets.configuration.serverbound

import io.github.dockyardmc.protocol.Packet
import io.netty.buffer.ByteBuf

class ServerboundFinishConfigurationPacket: Packet {

    override fun write(buffer: ByteBuf) {
    }

    companion object {
        fun read(buffer: ByteBuf): ServerboundFinishConfigurationPacket {
            return ServerboundFinishConfigurationPacket()
        }
    }
}