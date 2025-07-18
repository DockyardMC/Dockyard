package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.nbt.nbt
import io.github.dockyardmc.registry.DataDrivenRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.utils.kotlinx.ComponentToJsonSerializer
import kotlinx.serialization.Serializable
import net.kyori.adventure.nbt.CompoundBinaryTag

object TrimMaterialRegistry : DataDrivenRegistry<TrimMaterial>() {
    override val identifier: String = "minecraft:trim_material"
}

@Serializable
data class TrimMaterial(
    val identifier: String,
    val assetName: String,
    @Serializable(ComponentToJsonSerializer::class)
    val description: Component,
    val overrideArmorMaterials: Map<String, String>? = null,
) : RegistryEntry {

    override fun getProtocolId(): Int {
        return TrimMaterialRegistry.getProtocolIdByEntry(this)
    }

    override fun getEntryIdentifier(): String {
        return identifier
    }

    override fun getNbt(): CompoundBinaryTag {
        return nbt {
            withString("asset_name", assetName)
            withCompound("description", description.toNBT())
        }
    }
}