package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.dialog.Dialog
import io.github.dockyardmc.protocol.packets.configurations.ClientboundRegistryDataPacket
import io.github.dockyardmc.registry.DynamicRegistry
import io.github.dockyardmc.registry.RegistryEntry
import net.kyori.adventure.nbt.CompoundBinaryTag

object DialogRegistry : DynamicRegistry<DialogEntry>() {
    override val identifier: String = "minecraft:dialog"

    override fun updateCache() {
        cachedPacket = ClientboundRegistryDataPacket(this)
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
        return DialogRegistry.getProtocolIdByEntry(this)
    }

    override fun getEntryIdentifier(): String {
        return identifier
    }
}
