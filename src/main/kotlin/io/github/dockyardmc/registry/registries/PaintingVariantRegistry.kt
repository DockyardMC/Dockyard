package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.nbt.nbt
import io.github.dockyardmc.registry.DataDrivenRegistry
import io.github.dockyardmc.registry.RegistryEntry
import kotlinx.serialization.Serializable
import net.kyori.adventure.nbt.CompoundBinaryTag

object PaintingVariantRegistry : DataDrivenRegistry<PaintingVariant>() {

    override val identifier: String = "minecraft:painting_variant"

}

@Serializable
data class PaintingVariant(
    val identifier: String,
    val assetId: String,
    val height: Int,
    val width: Int,
) : RegistryEntry {

    override fun getProtocolId(): Int {
        return PaintingVariantRegistry.getProtocolEntries().getByValue(this)
    }

    override fun getEntryIdentifier(): String {
        return identifier
    }

    override fun getNbt(): CompoundBinaryTag {
        return nbt {
            withString("asset_id", assetId)
            withInt("height", height)
            withInt("width", width)
        }
    }
}