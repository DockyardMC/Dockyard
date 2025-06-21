package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.nbt.nbt
import io.github.dockyardmc.registry.DataDrivenRegistry
import io.github.dockyardmc.registry.RegistryEntry
import kotlinx.serialization.Serializable
import net.kyori.adventure.nbt.CompoundBinaryTag

object WolfVariantRegistry : DataDrivenRegistry<WolfVariant>() {

    override val identifier: String = "minecraft:wolf_variant"

}

@Serializable
data class WolfVariant(
    val identifier: String,
    val angry: String,
    val tame: String,
    val wild: String,
) : RegistryEntry {

    override fun getProtocolId(): Int {
        return WolfVariantRegistry.getProtocolEntries().getByValue(this)
    }

    override fun getEntryIdentifier(): String {
        return identifier
    }


    override fun getNbt(): CompoundBinaryTag {
        return nbt {
            withCompound("assets") {
                withString("angry", angry)
                withString("tame", tame)
                withString("wild", wild)
            }
        }
    }
}