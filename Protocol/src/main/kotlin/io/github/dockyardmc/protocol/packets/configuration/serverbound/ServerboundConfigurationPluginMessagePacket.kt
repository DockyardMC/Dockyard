package io.github.dockyardmc.protocol.packets.configuration.serverbound

import io.github.dockyardmc.protocol.Packet
import io.github.dockyardmc.protocol.writers.readString
import io.github.dockyardmc.protocol.writers.writeString
import io.netty.buffer.ByteBuf

class ServerboundConfigurationPluginMessagePacket(val channel: String, val data: ByteBuf): Packet {

    override fun write(buffer: ByteBuf) {
        buffer.writeString(channel)
        buffer.writeBytes(data)
    }

    companion object {
        fun read(buffer: ByteBuf): ServerboundConfigurationPluginMessagePacket {
            return ServerboundConfigurationPluginMessagePacket(
                buffer.readString(),
                buffer.readBytes(buffer.readableBytes())
            )
        }
    }
}