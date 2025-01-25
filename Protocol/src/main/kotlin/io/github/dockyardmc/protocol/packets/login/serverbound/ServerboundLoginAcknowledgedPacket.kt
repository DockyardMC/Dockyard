package io.github.dockyardmc.protocol.packets.login.serverbound

import io.github.dockyardmc.protocol.Packet
import io.netty.buffer.ByteBuf

class ServerboundLoginAcknowledgedPacket(): Packet {

    override fun write(buffer: ByteBuf) {

    }

    companion object {
        fun read(buffer: ByteBuf): ServerboundLoginAcknowledgedPacket {
            return ServerboundLoginAcknowledgedPacket()
        }
    }
}