package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.nbt.nbt
import io.github.dockyardmc.registry.DataDrivenRegistry
import io.github.dockyardmc.registry.RegistryEntry
import kotlinx.serialization.Serializable
import net.kyori.adventure.nbt.CompoundBinaryTag

object CatVariantRegistry : DataDrivenRegistry<CatVariant>() {
    override val identifier: String = "minecraft:cat_variant"
}

@Serializable
data class CatVariant(
    val identifier: String,
    val assetId: String,
) : RegistryEntry {

    override fun getEntryIdentifier(): String {
        return identifier
    }

    override fun getProtocolId(): Int {
        return CatVariantRegistry.getProtocolEntries().getByValue(this)
    }

    override fun getNbt(): CompoundBinaryTag {
        return nbt {
            withString("asset_id", assetId)
        }
    }
}