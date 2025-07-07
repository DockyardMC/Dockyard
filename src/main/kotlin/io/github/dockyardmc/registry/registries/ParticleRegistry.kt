package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.registry.DataDrivenRegistry
import io.github.dockyardmc.registry.RegistryEntry
import kotlinx.serialization.Serializable

object ParticleRegistry : DataDrivenRegistry<Particle>() {

    override val identifier: String = "minecraft:particle"
}

@Serializable
data class Particle(
    val identifier: String,
    val overrideLimiter: Boolean
) : RegistryEntry {

    override fun getProtocolId(): Int {
        return ParticleRegistry.getProtocolIdByEntry(this)
    }

    override fun getEntryIdentifier(): String {
        return identifier
    }
}