package io.github.dockyardmc.protocol.packets.configuration.serverbound

import io.github.dockyardmc.protocol.Packet
import io.github.dockyardmc.protocol.types.ResourcepackStatus
import io.github.dockyardmc.protocol.writers.readEnum
import io.github.dockyardmc.protocol.writers.readUUID
import io.github.dockyardmc.protocol.writers.writeEnum
import io.github.dockyardmc.protocol.writers.writeUUID
import io.netty.buffer.ByteBuf
import java.util.*

class ServerboundConfigurationResourcepackStatusPacket(
    var uuid: UUID,
    var status: ResourcepackStatus
): Packet {

    override fun write(buffer: ByteBuf) {
        buffer.writeUUID(uuid)
        buffer.writeEnum(status)
    }

    companion object {
        fun read(buffer: ByteBuf): ServerboundConfigurationResourcepackStatusPacket {
            return ServerboundConfigurationResourcepackStatusPacket(
                buffer.readUUID(),
                buffer.readEnum()
            )
        }
    }
}