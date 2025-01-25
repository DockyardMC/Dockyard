package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.extentions.getOrThrow
import io.github.dockyardmc.extentions.reversed
import io.github.dockyardmc.registry.DataDrivenRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.registry.RegistryException
import io.github.dockyardmc.utils.vectors.Vector3d
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import java.io.InputStream
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.GZIPInputStream

object EntityTypeRegistry: DataDrivenRegistry {
    override val identifier: String = "minecraft:entity_type"

    var entityTypes: MutableMap<String, EntityType> = mutableMapOf()
    var protocolIds: MutableMap<String, Int> = mutableMapOf()
    private val protocolIdCounter = AtomicInteger()

    override fun getMaxProtocolId(): Int {
        return protocolIdCounter.get()
    }

    private fun addEntry(entry: EntityType) {
        protocolIds[entry.identifier] = protocolIdCounter.getAndIncrement()
        entityTypes[entry.identifier] = entry
    }

    fun addEntries(entries: Collection<EntityType>) {
        entries.forEach { addEntry(it) }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun initialize(inputStream: InputStream) {
        val stream = GZIPInputStream(inputStream)
        val list = Json.decodeFromStream<List<EntityType>>(stream)
        addEntries(list)
    }

    override fun get(identifier: String): EntityType {
        return entityTypes[identifier] ?: throw RegistryException(identifier, this.getMap().size)
    }

    override fun getOrNull(identifier: String): EntityType? {
        return entityTypes[identifier]
    }

    override fun getByProtocolId(id: Int): EntityType {
        val identifier = protocolIds.reversed()[id] ?: throw RegistryException(id, this.getMap().size)
        return entityTypes.getOrThrow(identifier)
    }

    override fun getMap(): Map<String, EntityType> {
        return entityTypes
    }
}

@Serializable
data class EntityType(
    val identifier: String,
    val displayName: String,
    val category: String,
    val despawnDistance: Int,
    val isFriendly: Boolean,
    val isPersistent: Boolean,
    val maxInstancesPerChunk: Int,
    val noDespawnDistance: Int,
    val immuneToFire: Boolean,
    val immuneBlocks: List<String>,
    val dimensions: EntityDimensions,
): RegistryEntry {

    override fun getProtocolId(): Int {
        return EntityTypeRegistry.protocolIds.getOrThrow(identifier)
    }

    override fun getNbt(): NBTCompound? = null
}

@Serializable
data class EntityDimensions(
    val eyeHeight: Float,
    val fixed: Boolean,
    val height: Float,
    val width: Float,
    val nameTagLocation: Vector3d?,
    val passengerLocations: List<Vector3d>?,
    val vehicleLocation: Vector3d?,
    val wardenChestLocation: Vector3d?
)