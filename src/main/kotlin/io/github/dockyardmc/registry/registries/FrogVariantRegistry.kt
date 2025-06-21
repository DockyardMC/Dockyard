package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.nbt.nbt
import io.github.dockyardmc.registry.DataDrivenRegistry
import io.github.dockyardmc.registry.RegistryEntry
import kotlinx.serialization.Serializable
import net.kyori.adventure.nbt.CompoundBinaryTag

object FrogVariantRegistry : DataDrivenRegistry<FrogVariant>() {
    override val identifier: String = "minecraft:frog_variant"
}

@Serializable
data class FrogVariant(
    val identifier: String,
    val assetId: String,
) : RegistryEntry {

    override fun getProtocolId(): Int {
        return FrogVariantRegistry.getProtocolEntries().getByValue(this)
    }

    override fun getNbt(): CompoundBinaryTag {
        return nbt {
            withString("asset_id", assetId)
        }
    }

    override fun getEntryIdentifier(): String {
        return identifier
    }

}