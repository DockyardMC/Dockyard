package io.github.dockyardmc.protocol.packets.configuration.clientbound

import io.github.dockyardmc.protocol.Packet
import io.github.dockyardmc.protocol.registry.ProtocolRegistry
import io.github.dockyardmc.protocol.registry.Registry
import io.github.dockyardmc.protocol.registry.RegistryEntry
import io.github.dockyardmc.protocol.writers.*
import io.netty.buffer.ByteBuf

class ClientboundRegistryDataPacket(val registry: Registry): Packet {

    override fun write(buffer: ByteBuf) {
        buffer.writeString(registry.identifier)
        buffer.writeList(registry.getMap().values, RegistryEntry::write)
    }

    companion object {
        fun read(buffer: ByteBuf): ClientboundRegistryDataPacket {
            val identifier = buffer.readString()
            val entries = buffer.readList(RegistryEntry::read)

            return ClientboundRegistryDataPacket(ProtocolRegistry(identifier, entries.associateBy { it.getIdentifier() }))
        }
    }
}