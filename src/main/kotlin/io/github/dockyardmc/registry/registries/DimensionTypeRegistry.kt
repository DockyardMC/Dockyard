package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.extentions.getOrThrow
import io.github.dockyardmc.extentions.put
import io.github.dockyardmc.protocol.packets.configurations.ClientboundRegistryDataPacket
import io.github.dockyardmc.registry.DynamicRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import java.util.concurrent.atomic.AtomicInteger

object DimensionTypeRegistry : DynamicRegistry {

    override val identifier: String = "minecraft:dimension_type"

    private lateinit var cachedPacket: ClientboundRegistryDataPacket

    var dimensionTypes: MutableMap<String, DimensionType> = mutableMapOf()
    var protocolIds: MutableMap<String, Int> = mutableMapOf()
    private val protocolIdCounter =  AtomicInteger()

    fun addEntry(entry: DimensionType, updateCache: Boolean = true) {
        protocolIds[entry.identifier] = protocolIdCounter.getAndIncrement()
        dimensionTypes[entry.identifier] = entry
        if(updateCache) updateCache()
    }

    override fun register() {
         addEntry(DimensionType(
            "minecraft:overworld",
            ambientLight = 0.0f,
            bedWorks = true,
            coordinateScale = 1.0,
            effects = "minecraft:overworld",
            hasCeiling = false,
            hasRaids = true,
            hasSkylight = true,
            height = 384,
            infiniburn = "#minecraft:infiniburn_overworld",
            logicalHeight = 384,
            minY = -64,
            monsterSpawnBlockLightLimit = 0,
            monsterSpawnLightLevel = MonsterSpawnLightLevel(7, 0, "minecraft:uniform"),
            natural = true,
            piglinSafe = false,
            respawnAnchorWorks = false,
            ultraWarm = false,
        ))
        addEntry(DimensionType(
            "minecraft:overworld_caves",
            ambientLight = 0.0f,
            bedWorks = true,
            coordinateScale = 1.0,
            effects = "minecraft:overworld",
            hasCeiling = true,
            hasRaids = true,
            hasSkylight = true,
            height = 384,
            infiniburn = "#minecraft:infiniburn_overworld",
            logicalHeight = 384,
            minY = -64,
            monsterSpawnBlockLightLimit = 0,
            monsterSpawnLightLevel = MonsterSpawnLightLevel(7, 0, "minecraft:uniform"),
            natural = true,
            piglinSafe = false,
            respawnAnchorWorks = false,
            ultraWarm = false,
        ))
        addEntry(DimensionType(
            "minecraft:the_end",
            ambientLight = 0.0f,
            bedWorks = false,
            coordinateScale = 1.0,
            effects = "minecraft:the_end",
            hasCeiling = false,
            hasRaids = true,
            hasSkylight = false,
            height = 256,
            infiniburn = "#minecraft:infiniburn_end",
            logicalHeight = 256,
            minY = 0,
            monsterSpawnBlockLightLimit = 0,
            monsterSpawnLightLevel = MonsterSpawnLightLevel(7, 0, "minecraft:uniform"),
            natural = false,
            piglinSafe = false,
            respawnAnchorWorks = false,
            ultraWarm = false,
            fixedTime = 6000L,
        ))
        addEntry(DimensionType(
            "minecraft:the_nether",
            ambientLight = 0.1f,
            bedWorks = false,
            coordinateScale = 8.0,
            effects = "minecraft:the_nether",
            hasCeiling = true,
            hasRaids = false,
            hasSkylight = false,
            height = 256,
            infiniburn = "#minecraft:infiniburn_nether",
            logicalHeight = 128,
            minY = 0,
            monsterSpawnBlockLightLimit = 15,
            monsterSpawnLightLevel = MonsterSpawnLightLevel(7, 7, "minecraft:uniform"),
            natural = false,
            piglinSafe = true,
            respawnAnchorWorks = true,
            ultraWarm = true,
            fixedTime = 18000L,
        ))
    }

    override fun getCachedPacket(): ClientboundRegistryDataPacket {
        if(!DimensionTypeRegistry::cachedPacket.isInitialized) updateCache()
        return cachedPacket
    }

    override fun updateCache() {
        cachedPacket = ClientboundRegistryDataPacket(this)
    }

    override fun get(identifier: String): DimensionType {
        return dimensionTypes[identifier] ?: throw IllegalStateException("There is no registry entry with identifier $identifier")
    }

    override fun getOrNull(identifier: String): DimensionType? {
        return dimensionTypes[identifier]
    }

    override fun getByProtocolId(id: Int): DimensionType {
        return dimensionTypes.values.toList().getOrNull(id)
            ?: throw IllegalStateException("There is no registry entry with protocol id $id")
    }

    override fun getMap(): Map<String, DimensionType> {
        return dimensionTypes
    }
}

data class DimensionType(
    val identifier: String,
    val ambientLight: Float,
    val bedWorks: Boolean,
    val coordinateScale: Double,
    val effects: String,
    val hasCeiling: Boolean,
    val hasRaids: Boolean,
    val hasSkylight: Boolean,
    val height: Int,
    val infiniburn: String,
    val logicalHeight: Int,
    val minY: Int,
    val monsterSpawnBlockLightLimit: Int,
    val monsterSpawnLightLevel: MonsterSpawnLightLevel,
    val natural: Boolean,
    val piglinSafe: Boolean,
    val respawnAnchorWorks: Boolean,
    val ultraWarm: Boolean,
    val fixedTime: Long? = null,
) : RegistryEntry {

    override fun getProtocolId(): Int {
        return DimensionTypeRegistry.protocolIds.getOrThrow(identifier)
    }

    override fun getNbt(): NBTCompound {
        return NBT.Compound {
            it.put("ambient_light", ambientLight)
            it.put("bed_works", bedWorks)
            it.put("coordinate_scale", coordinateScale)
            it.put("effects", effects)
            if (fixedTime != null) it.put("fixed_time", fixedTime)
            it.put("has_ceiling", hasCeiling)
            it.put("has_raids", hasRaids)
            it.put("has_skylight", hasSkylight)
            it.put("height", height)
            it.put("infiniburn", infiniburn)
            it.put("logical_height", logicalHeight)
            it.put("min_y", minY)
            it.put("monster_spawn_block_light_limit", monsterSpawnBlockLightLimit)
            it.put("monster_spawn_light_level", monsterSpawnLightLevel.toNBT())
            it.put("natural", natural)
            it.put("piglin_safe", piglinSafe)
            it.put("respawn_anchor_works", respawnAnchorWorks)
            it.put("ultrawarm", ultraWarm)
        }
    }
}

data class MonsterSpawnLightLevel(
    val maxInclusive: Int,
    val minInclusive: Int,
    val type: String,
) {
    fun toNBT(): NBTCompound {
        return NBT.Compound {
            it.put("max_inclusive", maxInclusive)
            it.put("min_inclusive", minInclusive)
            it.put("type", type)
        }
    }
}