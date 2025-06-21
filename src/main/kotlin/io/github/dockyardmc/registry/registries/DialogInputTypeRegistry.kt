package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.protocol.packets.configurations.ClientboundRegistryDataPacket
import io.github.dockyardmc.registry.DynamicRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.registry.RegistryException
import net.kyori.adventure.nbt.CompoundBinaryTag
import java.util.concurrent.atomic.AtomicInteger

object DialogInputTypeRegistry : DynamicRegistry {
    override val identifier: String = "minecraft:input_control_type"

    private val dialogInputTypes: MutableMap<String, DialogInputType> = mutableMapOf()
    private val _protocolIds: MutableMap<String, Int> = mutableMapOf()
    private val protocolIdCounter = AtomicInteger()

    val protocolIds get() = _protocolIds.toMap()

    private lateinit var cachedPacket: ClientboundRegistryDataPacket

    override fun getMaxProtocolId(): Int = protocolIdCounter.get()

    private fun addEntry(entry: DialogInputType) {
        _protocolIds[entry.identifier] = protocolIdCounter.getAndIncrement()
        dialogInputTypes[entry.identifier] = entry
    }

    override fun register() {
        addEntry(DialogInputType("minecraft:boolean"))
        addEntry(DialogInputType("minecraft:number_range"))
        addEntry(DialogInputType("minecraft:single_option"))
        addEntry(DialogInputType("minecraft:text"))
        updateCache()
    }

    override fun getCachedPacket(): ClientboundRegistryDataPacket {
        if(!::cachedPacket.isInitialized) updateCache()
        return cachedPacket
    }

    override fun updateCache() {
        cachedPacket = ClientboundRegistryDataPacket(this)
    }

    override fun get(identifier: String): DialogInputType {
        return dialogInputTypes[identifier] ?: throw RegistryException(identifier, dialogInputTypes.size)
    }

    override fun getOrNull(identifier: String): DialogInputType? {
        return dialogInputTypes[identifier]
    }

    fun getProtocolId(identifier: String): Int = _protocolIds[identifier] ?: throw RegistryException(identifier, dialogInputTypes.size)

    override fun getByProtocolId(id: Int): RegistryEntry {
        return protocolIds.entries
            .first { entry -> entry.value == id }
            .let { entry ->
                dialogInputTypes[entry.key] ?: throw RegistryException(id, dialogInputTypes.size)
            }
    }

    override fun getMap(): Map<String, RegistryEntry> {
        return dialogInputTypes.toMap()
    }
}

data class DialogInputType(
    val identifier: String,
) : RegistryEntry {
    override fun getNbt(): CompoundBinaryTag? {
        return null
    }

    override fun getProtocolId(): Int {
        return DialogInputTypeRegistry.getProtocolId(identifier)
    }

    override fun getEntryIdentifier(): String {
        return identifier
    }
}
