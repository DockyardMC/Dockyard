package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.nbt.nbt
import io.github.dockyardmc.registry.DataDrivenRegistry
import io.github.dockyardmc.registry.RegistryEntry
import kotlinx.serialization.Serializable
import net.kyori.adventure.nbt.CompoundBinaryTag

object ChickenVariantRegistry : DataDrivenRegistry<ChickenVariant>() {
    override val identifier: String = "minecraft:chicken_variant"
}

@Serializable
data class ChickenVariant(
    val identifier: String,
    val assetId: String,
) : RegistryEntry {

    override fun getProtocolId(): Int {
        return ChickenVariantRegistry.getProtocolIdByEntry(this)
    }

    override fun getEntryIdentifier(): String {
        return identifier
    }

    override fun getNbt(): CompoundBinaryTag {
        return nbt {
            withString("asset_id", assetId)
        }
    }
}