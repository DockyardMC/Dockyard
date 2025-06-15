package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.dialog.Dialog
import io.github.dockyardmc.protocol.packets.configurations.ClientboundRegistryDataPacket
import io.github.dockyardmc.registry.DynamicRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.registry.RegistryException
import net.kyori.adventure.nbt.CompoundBinaryTag
import java.util.concurrent.atomic.AtomicInteger

object DialogRegistry : DynamicRegistry {
    override val identifier: String = "minecraft:dialog"

    private val dialogs: MutableMap<String, DialogEntry> = mutableMapOf()
    private val _protocolIds: MutableMap<String, Int> = mutableMapOf()
    private val protocolIdCounter = AtomicInteger()

    val protocolIds get() = _protocolIds.toMap()

    private lateinit var cachedPacket: ClientboundRegistryDataPacket

    override fun getMaxProtocolId(): Int = protocolIdCounter.get()

    fun addEntry(entry: DialogEntry) {
        _protocolIds[entry.identifier] = protocolIdCounter.getAndIncrement()
        dialogs[entry.identifier] = entry

        updateCache()
    }

    fun addEntry(identifier: String, dialog: Dialog): DialogEntry {
        return DialogEntry(identifier, dialog).also(::addEntry)
    }

    override fun register() {
    }

    override fun getCachedPacket(): ClientboundRegistryDataPacket {
        if(!::cachedPacket.isInitialized) updateCache()
        return cachedPacket
    }

    override fun updateCache() {
        cachedPacket = ClientboundRegistryDataPacket(this)
    }

    override fun get(identifier: String): DialogEntry {
        return dialogs[identifier] ?: throw RegistryException(identifier, dialogs.size)
    }

    override fun getOrNull(identifier: String): DialogEntry? {
        return dialogs[identifier]
    }

    fun getProtocolId(identifier: String): Int = _protocolIds[identifier] ?: throw RegistryException(identifier, dialogs.size)

    override fun getByProtocolId(id: Int): DialogEntry {
        return protocolIds.entries
            .first { entry -> entry.value == id }
            .let { entry ->
                dialogs[entry.key] ?: throw RegistryException(id, dialogs.size)
            }
    }

    override fun getMap(): Map<String, DialogEntry> {
        return dialogs.toMap()
    }
}

data class DialogEntry(
    val identifier: String,
    val dialog: Dialog
) : RegistryEntry {
    override fun getNbt(): CompoundBinaryTag {
        return dialog.getNbt()
    }

    override fun getProtocolId(): Int {
        return DialogRegistry.getProtocolId(identifier)
    }

    override fun getEntryIdentifier(): String {
        return identifier
    }
}
