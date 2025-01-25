package io.github.dockyardmc.protocol.packets.configuration.clientbound

import io.github.dockyardmc.protocol.Packet
import io.github.dockyardmc.protocol.writers.readList
import io.github.dockyardmc.protocol.writers.readString
import io.github.dockyardmc.protocol.writers.writeList
import io.github.dockyardmc.protocol.writers.writeString
import io.netty.buffer.ByteBuf

class ClientboundFeatureFlagsPacket(val featureFlags: Collection<String>): Packet {

    override fun write(buffer: ByteBuf) {
        buffer.writeList<String>(featureFlags, ByteBuf::writeString)
    }

    companion object {
        fun read(buffer: ByteBuf): ClientboundFeatureFlagsPacket {
            return ClientboundFeatureFlagsPacket(buffer.readList(ByteBuf::readString))
        }
    }

}