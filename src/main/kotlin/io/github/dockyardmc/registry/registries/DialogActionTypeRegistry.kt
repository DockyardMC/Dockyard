package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.protocol.packets.configurations.ClientboundRegistryDataPacket
import io.github.dockyardmc.registry.DynamicRegistry
import io.github.dockyardmc.registry.RegistryEntry
import net.kyori.adventure.nbt.CompoundBinaryTag

object DialogActionTypeRegistry : DynamicRegistry<DialogActionType>() {
    override val identifier: String = "minecraft:dialog_action_type"

    init {
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

    override fun updateCache() {
        cachedPacket = ClientboundRegistryDataPacket(this)
    }
}

data class DialogActionType(
    val identifier: String,
) : RegistryEntry {
    override fun getNbt(): CompoundBinaryTag? {
        return null
    }

    override fun getProtocolId(): Int {
        return DialogActionTypeRegistry.getProtocolEntries().getByValue(this)
    }

    override fun getEntryIdentifier(): String {
        return identifier
    }
}
