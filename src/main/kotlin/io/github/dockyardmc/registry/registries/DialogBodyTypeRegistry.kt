package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.dialog.body.DialogBody
import io.github.dockyardmc.dialog.body.DialogItemBody
import io.github.dockyardmc.dialog.body.PlainMessage
import io.github.dockyardmc.protocol.packets.configurations.ClientboundRegistryDataPacket
import io.github.dockyardmc.registry.DynamicRegistry
import io.github.dockyardmc.registry.RegistryEntry
import net.kyori.adventure.nbt.CompoundBinaryTag
import kotlin.reflect.KClass

object DialogBodyTypeRegistry : DynamicRegistry<DialogBodyType>() {
    override val identifier: String = "minecraft:dialog_body_type"

    init {
        addEntry(DialogBodyType("minecraft:item", DialogItemBody::class))
        addEntry(DialogBodyType("minecraft:plain_message", PlainMessage::class))
    }

    override fun updateCache() {
        cachedPacket = ClientboundRegistryDataPacket(this)
    }
}

data class DialogBodyType(
    val identifier: String,
    val clazz: KClass<out DialogBody>
) : RegistryEntry {
    override fun getNbt(): CompoundBinaryTag? {
        return null
    }

    override fun getProtocolId(): Int {
        return DialogBodyTypeRegistry.getProtocolIdByEntry(this)
    }

    override fun getEntryIdentifier(): String {
        return identifier
    }
}