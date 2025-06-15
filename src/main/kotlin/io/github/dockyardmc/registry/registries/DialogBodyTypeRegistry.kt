package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.dialog.body.DialogBody
import io.github.dockyardmc.dialog.body.DialogItemBody
import io.github.dockyardmc.dialog.body.PlainMessage
import io.github.dockyardmc.protocol.packets.configurations.ClientboundRegistryDataPacket
import io.github.dockyardmc.registry.*
import net.kyori.adventure.nbt.CompoundBinaryTag
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

object DialogBodyTypeRegistry : DynamicRegistry {
    override val identifier: String = "minecraft:dialog_body_type"

    private val dialogBodyTypes: MutableMap<String, DialogBodyType> = mutableMapOf()
    private val _protocolIds: MutableMap<String, Int> = mutableMapOf()
    private val protocolIdCounter = AtomicInteger()

    val protocolIds get() = _protocolIds.toMap()

    private lateinit var cachedPacket: ClientboundRegistryDataPacket

    override fun getMaxProtocolId(): Int = protocolIdCounter.get()

    fun addEntry(entry: DialogBodyType) {
        _protocolIds[entry.identifier] = protocolIdCounter.getAndIncrement()
        dialogBodyTypes[entry.identifier] = entry
    }

    override fun register() {
        addEntry(DialogBodyType("minecraft:item", DialogItemBody::class))
        addEntry(DialogBodyType("minecraft:plain_message", PlainMessage::class))
    }

    override fun getCachedPacket(): ClientboundRegistryDataPacket {
        if (!::cachedPacket.isInitialized) updateCache()
        return cachedPacket
    }

    override fun updateCache() {
        cachedPacket = ClientboundRegistryDataPacket(this)
    }

    override fun get(identifier: String): DialogBodyType {
        return dialogBodyTypes[identifier] ?: throw RegistryException(identifier, dialogBodyTypes.size)
    }

    override fun getOrNull(identifier: String): DialogBodyType? = dialogBodyTypes[identifier]

    fun getProtocolId(identifier: String): Int = _protocolIds[identifier] ?: throw RegistryException(identifier, dialogBodyTypes.size)

    override fun getByProtocolId(id: Int): RegistryEntry {
        return protocolIds.entries
            .first { entry -> entry.value == id }
            .let { entry ->
                dialogBodyTypes[entry.key] ?: throw RegistryException(id, dialogBodyTypes.size)
            }
    }

    override fun getMap(): Map<String, RegistryEntry> = dialogBodyTypes.toMap()
}

data class DialogBodyType(
    val identifier: String,
    val clazz: KClass<out DialogBody>
) : RegistryEntry {
    override fun getNbt(): CompoundBinaryTag? {
        return null
    }

    override fun getProtocolId(): Int {
        return DialogBodyTypeRegistry.getProtocolId(identifier)
    }

    override fun getEntryIdentifier(): String {
        return identifier
    }
}