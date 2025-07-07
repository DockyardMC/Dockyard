package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.nbt.nbt
import io.github.dockyardmc.registry.DataDrivenRegistry
import io.github.dockyardmc.registry.RegistryEntry
import kotlinx.serialization.Serializable
import net.kyori.adventure.nbt.CompoundBinaryTag

object BannerPatternRegistry : DataDrivenRegistry<BannerPattern>() {
    override val identifier: String = "minecraft:banner_pattern"
}

@Serializable
data class BannerPattern(
    val identifier: String,
    val translationKey: String,
) : RegistryEntry {

    override fun getProtocolId(): Int {
        return BannerPatternRegistry.getProtocolIdByEntry(this)
    }

    override fun getEntryIdentifier(): String {
        return identifier
    }

    override fun getNbt(): CompoundBinaryTag {
        return nbt {
            withString("asset_id", identifier)
            withString("translation_key", translationKey)
        }
    }
}
