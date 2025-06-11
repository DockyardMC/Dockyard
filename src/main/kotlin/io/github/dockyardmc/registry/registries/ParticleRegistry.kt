package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.extentions.getOrThrow
import io.github.dockyardmc.registry.DataDrivenRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.registry.RegistryException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.InputStream
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.GZIPInputStream

@OptIn(ExperimentalSerializationApi::class)
object ParticleRegistry : DataDrivenRegistry {

    override val identifier: String = "minecraft:particle"

    val particles: MutableMap<String, Particle> = mutableMapOf()
    val protocolIds: MutableMap<Particle, Int> = mutableMapOf()
    private val protocolIdCounter = AtomicInteger()

    override fun getMaxProtocolId(): Int {
        return protocolIdCounter.get()
    }

    fun addEntry(entry: Particle) {
        protocolIds[entry] = protocolIdCounter.getAndIncrement()
        particles[entry.identifier] = entry
    }

    override fun initialize(inputStream: InputStream) {
        val stream = GZIPInputStream(inputStream)
        val list = Json.decodeFromStream<List<Particle>>(stream)
        list.forEach(::addEntry)
    }

    override fun get(identifier: String): Particle {
        return particles[identifier]
            ?: throw RegistryException(identifier, this.getMap().size)
    }

    override fun getOrNull(identifier: String): Particle? {
        return particles[identifier]
    }

    override fun getByProtocolId(id: Int): Particle {
        return particles.values.toList().getOrNull(id) ?: throw RegistryException(id, this.getMap().size)
    }

    override fun getMap(): Map<String, Particle> {
        return particles
    }
}

@Serializable
data class Particle(
    val identifier: String,
    val overrideLimiter: Boolean
) : RegistryEntry {

    override fun getProtocolId(): Int {
        return ParticleRegistry.protocolIds.getOrThrow(this)
    }

    override fun getEntryIdentifier(): String {
        return identifier
    }
}