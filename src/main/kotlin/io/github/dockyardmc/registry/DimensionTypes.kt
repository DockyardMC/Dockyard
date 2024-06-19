package io.github.dockyardmc.registry

import io.github.dockyardmc.extentions.put
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound

object DimensionTypes {

    var map = mutableMapOf<String, DimensionType>(
        "minecraft:overworld" to DimensionType(0.0f, true, 1.0, "minecraft:overworld", false, true ,true, 384, "#minecraft:infiniburn_overworld", 384, -64, 0, MonsterSpawnLightLevel(7, 0, "minecraft:uniform"), true, false, false, false),
        "minecraft:overworld_caves" to DimensionType(0.0f, true, 1.0, "minecraft:overworld", true, true, true, 384, "#minecraft:infiniburn_overworld", 384, -64, 0, MonsterSpawnLightLevel(7, 0, "minecraft:uniform"), true, false, false, false),
        "minecraft:the_end" to DimensionType(0.0f, false, 1.0, "minecraft:the_end", false, true, false, 256, "#minecraft:infiniburn_end", 256, 0, 0, MonsterSpawnLightLevel(7, 0, "minecraft:uniform"), false, false, false, false, 6000L),
        "minecraft:the_nether" to DimensionType(0.1f, false, 8.0, "minecraft:the_nether", true, false, false, 256, "#minecraft:infiniburn_nether", 128, 0, 15, MonsterSpawnLightLevel(7, 7, "minecraft:uniform"), false, true, true, true, 18000L)
    )

    val OVERWORLD =  map["minecraft:overworld"]!!
    val OVERWORLD_CAVES =  map["minecraft:overworld_caves"]!!
    val THE_END =  map["minecraft:the_end"]!!
    val NETHER =  map["minecraft:the_nether"]!!

    lateinit var registryCache: Registry

    fun cacheRegistry() {
        val registryEntries = mutableListOf<RegistryEntry>()
        map.forEach { registryEntries.add(RegistryEntry(it.key, it.value.toNBT())) }

        registryCache = Registry("minecraft:dimension_type", registryEntries)
    }

    init {
        cacheRegistry()
    }
}

data class DimensionType(
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
    val fixedTime: Long? = null
) {

    val id: Int get() = DimensionTypes.map.values.indexOf(this)

    fun toNBT(): NBTCompound {
        return NBT.Compound {
            it.put("ambient_light", ambientLight)
            it.put("bed_works", bedWorks)
            it.put("coordinate_scale", coordinateScale)
            it.put("effects", effects)
            if(fixedTime != null) it.put("fixed_time", fixedTime)
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
    val type: String
) {
    fun toNBT(): NBTCompound {
        return NBT.Compound {
            it.put("max_inclusive", maxInclusive)
            it.put("min_inclusive", minInclusive)
            it.put("type", type)
        }
    }
}

