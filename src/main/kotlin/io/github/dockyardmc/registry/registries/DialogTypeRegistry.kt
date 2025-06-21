package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.protocol.packets.configurations.ClientboundRegistryDataPacket
import io.github.dockyardmc.registry.DynamicRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.registry.RegistryException
import net.kyori.adventure.nbt.CompoundBinaryTag
import java.util.concurrent.atomic.AtomicInteger

object DialogTypeRegistry : DynamicRegistry {
    override val identifier: String = "minecraft:dialog_type"

    private val dialogTypes: MutableMap<String, DialogType> = mutableMapOf()
    private val _protocolIds: MutableMap<String, Int> = mutableMapOf()
    private val protocolIdCounter = AtomicInteger()

    val protocolIds get() = _protocolIds.toMap()

    private lateinit var cachedPacket: ClientboundRegistryDataPacket

    override fun getMaxProtocolId(): Int = protocolIdCounter.get()

    fun addEntry(entry: DialogType) {
        _protocolIds[entry.identifier] = protocolIdCounter.getAndIncrement()
        dialogTypes[entry.identifier] = entry
    }

    override fun register() {
        addEntry(DialogType("minecraft:notice"))
        addEntry(DialogType("minecraft:server_links"))
        addEntry(DialogType("minecraft:dialog_list"))
        addEntry(DialogType("minecraft:multi_action"))
        addEntry(DialogType("minecraft:confirmation"))
    }

    override fun getCachedPacket(): ClientboundRegistryDataPacket {
        if(!::cachedPacket.isInitialized) updateCache()
        return cachedPacket
    }

    override fun updateCache() {
        cachedPacket = ClientboundRegistryDataPacket(this)
    }

    override fun get(identifier: String): DialogType {
        return dialogTypes[identifier] ?: throw RegistryException(identifier, dialogTypes.size)
    }

    override fun getOrNull(identifier: String): DialogType? {
        return dialogTypes[identifier]
    }

    fun getProtocolId(identifier: String): Int = _protocolIds[identifier] ?: throw RegistryException(identifier, dialogTypes.size)

    override fun getByProtocolId(id: Int): RegistryEntry {
        return protocolIds.entries
            .first { entry -> entry.value == id }
            .let { entry ->
                dialogTypes[entry.key] ?: throw RegistryException(id, dialogTypes.size)
            }
    }

    override fun getMap(): Map<String, RegistryEntry> {
        return dialogTypes.toMap()
    }
}

data class DialogType(
    val identifier: String,
) : RegistryEntry {
    override fun getNbt(): CompoundBinaryTag? {
        return null
    }

    override fun getProtocolId(): Int {
        return DialogTypeRegistry.getProtocolId(identifier)
    }

    override fun getEntryIdentifier(): String {
        return identifier
    }
}
