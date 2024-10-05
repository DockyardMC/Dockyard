package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.protocol.packets.configurations.ClientboundRegistryDataPacket
import io.github.dockyardmc.registry.DynamicRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.registry.RegistryException
import java.util.concurrent.atomic.AtomicInteger

object ChatTypeRegistry: DynamicRegistry {

    override val identifier: String = "minecraft:chat_type"

    private lateinit var cachedPacket: ClientboundRegistryDataPacket

    val chatTypes: MutableMap<String, RegistryEntry> = mutableMapOf()
    private val protocolIdCounter =  AtomicInteger()

    override fun register() {
        // Empty, dockyard does not use vanilla chat type stuff
    }

    override fun getCachedPacket(): ClientboundRegistryDataPacket {
        if(!::cachedPacket.isInitialized) updateCache()
        return cachedPacket
    }

    override fun updateCache() {
        cachedPacket = ClientboundRegistryDataPacket(this)
    }

    override fun get(identifier: String): RegistryEntry {
        return chatTypes[identifier] ?: throw RegistryException(identifier, this.getMap().size)
    }

    override fun getOrNull(identifier: String): RegistryEntry? {
        return chatTypes[identifier]
    }

    override fun getByProtocolId(id: Int): RegistryEntry {
        return chatTypes.values.toList().getOrNull(id) ?: throw RegistryException(id, this.getMap().size)
    }

    override fun getMap(): Map<String, RegistryEntry> {
        return chatTypes
    }
}