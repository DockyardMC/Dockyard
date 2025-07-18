package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.registry.DataDrivenRegistry
import io.github.dockyardmc.registry.RegistryEntry
import kotlinx.serialization.Serializable
import net.kyori.adventure.nbt.CompoundBinaryTag

object FluidRegistry : DataDrivenRegistry<Fluid>() {
    override val identifier: String = "minecraft:fluid"
}

@Serializable
data class Fluid(
    val identifier: String,
    val dripParticle: String?,
    val pickupSound: String,
    val explosionResistance: Float,
    val block: String
) : RegistryEntry {

    override fun getNbt(): CompoundBinaryTag? = null

    override fun getProtocolId(): Int {
        return FluidRegistry.getProtocolIdByEntry(this)
    }

    override fun getEntryIdentifier(): String {
        return identifier
    }

}