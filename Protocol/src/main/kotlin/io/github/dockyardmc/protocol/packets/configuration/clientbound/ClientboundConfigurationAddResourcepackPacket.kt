package io.github.dockyardmc.protocol.packets.configuration.clientbound

import io.github.dockyardmc.protocol.Packet
import io.github.dockyardmc.protocol.writers.*
import io.github.dockyardmc.scroll.Component
import io.netty.buffer.ByteBuf
import java.util.UUID

class ClientboundConfigurationAddResourcepackPacket(
    val uuid: UUID,
    val url: String,
    val hash: String,
    val forced: Boolean,
    val prompt: Component?
): Packet {

    override fun write(buffer: ByteBuf) {
        buffer.writeUUID(uuid)
        buffer.writeString(url)
        buffer.writeString(hash)
        buffer.writeBoolean(forced)
        buffer.writeOptional(prompt, ByteBuf::writeTextComponent)
    }

    companion object {
        fun read(buffer: ByteBuf): ClientboundConfigurationAddResourcepackPacket {
            return ClientboundConfigurationAddResourcepackPacket(
                buffer.readUUID(),
                buffer.readString(),
                buffer.readString(),
                buffer.readBoolean(),
                buffer.readOptional(ByteBuf::readTextComponent)
            )
        }
    }
}