package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.protocol.Packet
import io.github.dockyardmc.protocol.writers.readString
import io.github.dockyardmc.protocol.writers.writeString
import io.netty.buffer.ByteBuf

class ServerboundChatCommandPacket(val command: String): Packet {

    override fun write(buffer: ByteBuf) {
        buffer.writeString(command)
    }

    companion object {
        fun read(buffer: ByteBuf): ServerboundChatCommandPacket {
            return ServerboundChatCommandPacket(buffer.readString())
        }
    }
}