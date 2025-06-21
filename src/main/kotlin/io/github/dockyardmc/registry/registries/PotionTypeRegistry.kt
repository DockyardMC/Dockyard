package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.registry.DataDrivenRegistry
import io.github.dockyardmc.registry.RegistryEntry
import kotlinx.serialization.Serializable
import net.kyori.adventure.nbt.BinaryTag
import net.kyori.adventure.nbt.CompoundBinaryTag

object PotionTypeRegistry : DataDrivenRegistry<PotionType>() {
    override val identifier: String = "minecraft:potion"
}

@Serializable
data class PotionType(
    val identifier: String,
) : RegistryEntry {

    override fun getProtocolId(): Int {
        return PotionTypeRegistry.getProtocolEntries().getByValue(this)
    }

    override fun getNbt(): BinaryTag {
        return CompoundBinaryTag.empty()
    }

    override fun getEntryIdentifier(): String {
        return identifier
    }
}