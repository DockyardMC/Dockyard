package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.protocol.packets.configurations.ClientboundRegistryDataPacket
import io.github.dockyardmc.registry.DynamicRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.registry.RegistryException
import net.kyori.adventure.nbt.CompoundBinaryTag
import java.util.concurrent.atomic.AtomicInteger

object DialogActionTypeRegistry : DynamicRegistry {
    override val identifier: String = "minecraft:dialog_action_type"

    private val submitMethodTypes: MutableMap<String, DialogActionType> = mutableMapOf()
    private val _protocolIds: MutableMap<String, Int> = mutableMapOf()
    private val protocolIdCounter = AtomicInteger()

    val protocolIds get() = _protocolIds.toMap()

    private lateinit var cachedPacket: ClientboundRegistryDataPacket

    override fun getMaxProtocolId(): Int = protocolIdCounter.get()

    private fun addEntry(entry: DialogActionType) {
        _protocolIds[entry.identifier] = protocolIdCounter.getAndIncrement()
        submitMethodTypes[entry.identifier] = entry
    }

    override fun register() {
        // this is not okay
        listOf(
            "open_url",
            "run_command",
            "suggest_command",
            "change_page",
            "copy_to_clipboard",
            "show_dialog",
            "custom",

            "dynamic/run_command",
            "dynamic/custom"
        ).forEach {
            addEntry(DialogActionType(it))
        }
        updateCache()
    }

    override fun getCachedPacket(): ClientboundRegistryDataPacket {
        if(!::cachedPacket.isInitialized) updateCache()
        return cachedPacket
    }

    override fun updateCache() {
        cachedPacket = ClientboundRegistryDataPacket(this)
    }

    override fun get(identifier: String): DialogActionType {
        return submitMethodTypes[identifier] ?: throw RegistryException(identifier, submitMethodTypes.size)
    }

    override fun getOrNull(identifier: String): DialogActionType? {
        return submitMethodTypes[identifier]
    }

    fun getProtocolId(identifier: String): Int = _protocolIds[identifier] ?: throw RegistryException(identifier, submitMethodTypes.size)

    override fun getByProtocolId(id: Int): RegistryEntry {
        return protocolIds.entries
            .first { entry -> entry.value == id }
            .let { entry ->
                submitMethodTypes[entry.key] ?: throw RegistryException(id, submitMethodTypes.size)
            }
    }

    override fun getMap(): Map<String, RegistryEntry> {
        return submitMethodTypes.toMap()
    }
}

data class DialogActionType(
    val identifier: String,
) : RegistryEntry {
    override fun getNbt(): CompoundBinaryTag? {
        return null
    }

    override fun getProtocolId(): Int {
        return DialogActionTypeRegistry.getProtocolId(identifier)
    }

    override fun getEntryIdentifier(): String {
        return identifier
    }
}
