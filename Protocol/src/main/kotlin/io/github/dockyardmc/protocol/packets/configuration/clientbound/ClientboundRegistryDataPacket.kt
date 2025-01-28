package io.github.dockyardmc.protocol.packets.configuration.clientbound

import io.github.dockyardmc.protocol.Packet
import io.github.dockyardmc.protocol.registry.Registry
import io.github.dockyardmc.protocol.registry.RegistryEntry
import io.github.dockyardmc.protocol.registry.RegistryManager
import io.github.dockyardmc.protocol.writers.*
import io.netty.buffer.ByteBuf
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.declaredMemberFunctions

class ClientboundRegistryDataPacket(val registry: Registry): Packet {

    override fun write(buffer: ByteBuf) {
        buffer.writeString(registry.identifier)
        buffer.writeList(registry.getMap().values, RegistryEntry::write)
    }

    companion object {
        fun read(buffer: ByteBuf): ClientboundRegistryDataPacket {
            val identifier = buffer.readString()
            val registry = RegistryManager.dynamicRegistries[identifier] ?: throw IllegalArgumentException("Registry with identifier $identifier does not exist!")

            val entries = buffer.readList(RegistryEntry::read)
            val clazz = registry.getEntryClass()

            val companionObject = clazz.companionObject ?: throw IllegalStateException("No companion object in registry entry")
            val readFunction = companionObject.declaredMemberFunctions.find { it.name == "read" } ?: throw IllegalStateException("No read function in registry entry")

            entries.forEach { entry ->
                readFunction.call(companionObject.objectInstance, entry)
            }

            return ClientboundRegistryDataPacket(ProtocolRegistry(identifier, entries.associateBy { it.getIdentifier() }))
        }
    }
}