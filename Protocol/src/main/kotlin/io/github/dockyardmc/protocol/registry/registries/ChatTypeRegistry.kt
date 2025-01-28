package io.github.dockyardmc.protocol.registry.registries

import io.github.dockyardmc.protocol.packets.configuration.clientbound.ClientboundRegistryDataPacket
import io.github.dockyardmc.protocol.registry.DynamicRegistry
import io.github.dockyardmc.protocol.registry.RegistryEntry
import io.github.dockyardmc.protocol.registry.RegistryException
import java.lang.IllegalArgumentException
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

object ChatTypeRegistry: DynamicRegistry {

    override val identifier: String = "minecraft:chat_type"

    private lateinit var cachedPacket: ClientboundRegistryDataPacket

    val chatTypes: MutableMap<String, RegistryEntry> = mutableMapOf()
    private val protocolIdCounter =  AtomicInteger()

    override fun getEntryClass(): KClass<out RegistryEntry> {
        throw IllegalArgumentException("we don't do chat type here")
    }

    override fun getMaxProtocolId(): Int {
        return protocolIdCounter.get()
    }

    override fun register() {
        // Empty, dockyard does not use vanilla chat type stuff
    }

    override fun getCachedPacket(): ClientboundRegistryDataPacket {
        if(!ChatTypeRegistry::cachedPacket.isInitialized) updateCache()
        return cachedPacket
    }

    override fun updateCache() {
        cachedPacket = ClientboundRegistryDataPacket(this)
    }

    override fun get(identifier: String): RegistryEntry {
        return chatTypes[identifier] ?: throw RegistryException(identifier, getMap().size)
    }

    override fun getOrNull(identifier: String): RegistryEntry? {
        return chatTypes[identifier]
    }

    override fun getByProtocolId(id: Int): RegistryEntry {
        return chatTypes.values.toList().getOrNull(id) ?: throw RegistryException(id, getMap().size)
    }

    override fun getMap(): Map<String, RegistryEntry> {
        return chatTypes
    }
}