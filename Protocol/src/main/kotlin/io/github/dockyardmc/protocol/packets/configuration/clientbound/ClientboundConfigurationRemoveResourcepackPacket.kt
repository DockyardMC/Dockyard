package io.github.dockyardmc.protocol.packets.configuration.clientbound

import io.github.dockyardmc.protocol.Packet
import io.github.dockyardmc.protocol.writers.readOptional
import io.github.dockyardmc.protocol.writers.readUUID
import io.github.dockyardmc.protocol.writers.writeOptional
import io.github.dockyardmc.protocol.writers.writeUUID
import io.netty.buffer.ByteBuf
import java.util.UUID

class ClientboundConfigurationRemoveResourcepackPacket(val uuid: UUID?): Packet {

    override fun write(buffer: ByteBuf) {
        buffer.writeOptional(uuid, ByteBuf::writeUUID)
    }

    companion object {
        fun read(buffer: ByteBuf): ClientboundConfigurationRemoveResourcepackPacket {
            return ClientboundConfigurationRemoveResourcepackPacket(buffer.readOptional(ByteBuf::readUUID))
        }
    }

}