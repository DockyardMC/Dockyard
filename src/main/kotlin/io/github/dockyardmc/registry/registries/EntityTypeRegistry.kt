package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.registry.DataDrivenRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.utils.debug
import io.github.dockyardmc.utils.vectors.Vector3d
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import java.io.InputStream
import java.lang.IllegalStateException
import java.util.zip.GZIPInputStream

object EntityTypeRegistry: DataDrivenRegistry {
    override val identifier: String = "minecraft:entity_type"

    var entityTypes: MutableMap<String, EntityType> = mutableMapOf()
    var protocolIdMap: MutableMap<Int, EntityType> = mutableMapOf()

    @OptIn(ExperimentalSerializationApi::class)
    override fun initialize(inputStream: InputStream) {
        val stream = GZIPInputStream(inputStream)
        val list = Json.decodeFromStream<List<EntityType>>(stream)
        entityTypes = list.associateBy { it.identifier }.toMutableMap()
        protocolIdMap = list.associateBy { it.protocolId }.toMutableMap()
        debug("Loaded entity type registry: ${entityTypes.size} entries", false)
    }

    override fun get(identifier: String): EntityType {
        return entityTypes[identifier] ?: throw IllegalStateException("Biome with identifier $identifier is not present in the registry!")
    }

    override fun getOrNull(identifier: String): EntityType? {
        return entityTypes[identifier]
    }

    override fun getByProtocolId(id: Int): EntityType {
        return protocolIdMap[id] ?: throw IllegalStateException("There is no registry entry with protocol id $id")
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
    override val protocolId: Int,
): RegistryEntry {
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