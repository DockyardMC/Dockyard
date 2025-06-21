package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.maths.vectors.Vector3d
import io.github.dockyardmc.registry.DataDrivenRegistry
import io.github.dockyardmc.registry.RegistryEntry
import kotlinx.serialization.Serializable
import net.kyori.adventure.nbt.CompoundBinaryTag

object EntityTypeRegistry : DataDrivenRegistry<EntityType>() {
    override val identifier: String = "minecraft:entity_type"
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
) : RegistryEntry {

    override fun getProtocolId(): Int {
        return EntityTypeRegistry.getProtocolEntries().getByValue(this)
    }

    override fun getEntryIdentifier(): String {
        return identifier
    }

    override fun getNbt(): CompoundBinaryTag? = null
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