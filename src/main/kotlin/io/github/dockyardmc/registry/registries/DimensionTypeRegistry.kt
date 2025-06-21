package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.nbt.nbt
import io.github.dockyardmc.protocol.packets.configurations.ClientboundRegistryDataPacket
import io.github.dockyardmc.registry.DynamicRegistry
import io.github.dockyardmc.registry.RegistryEntry
import net.kyori.adventure.nbt.CompoundBinaryTag

object DimensionTypeRegistry : DynamicRegistry<DimensionType>() {

    override val identifier: String = "minecraft:dimension_type"

    override fun updateCache() {
        cachedPacket = ClientboundRegistryDataPacket(this)
    }

    init {
        addEntry(
            DimensionType(
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
            )
        )
        addEntry(
            DimensionType(
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
            )
        )
        addEntry(
            DimensionType(
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
            )
        )
        addEntry(
            DimensionType(
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
            )
        )
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
    val cloudHeight: Int? = null
) : RegistryEntry {

    override fun getProtocolId(): Int {
        return DimensionTypeRegistry.getProtocolEntries().getByValue(this)
    }

    override fun getEntryIdentifier(): String {
        return identifier
    }


    override fun getNbt(): CompoundBinaryTag {
        val nbt = nbt {
            withFloat("ambient_light", ambientLight)
            withBoolean("bed_works", bedWorks)
            withDouble("coordinate_scale", coordinateScale)
            withString("effects", effects)
            if (fixedTime != null) withLong("fixed_time", fixedTime)
            withBoolean("has_ceiling", hasCeiling)
            withBoolean("has_raids", hasRaids)
            withBoolean("has_skylight", hasSkylight)
            withInt("height", height)
            withString("infiniburn", infiniburn)
            withInt("logical_height", logicalHeight)
            withInt("min_y", minY)
            withInt("monster_spawn_block_light_limit", monsterSpawnBlockLightLimit)
            withCompound("monster_spawn_light_level", monsterSpawnLightLevel.toNBT())
            withBoolean("natural", natural)
            withBoolean("piglin_safe", piglinSafe)
            withBoolean("respawn_anchor_works", respawnAnchorWorks)
            withBoolean("ultrawarm", ultraWarm)
            cloudHeight?.let { withInt("cloud_height", cloudHeight) }
        }
        return nbt
    }
}

data class MonsterSpawnLightLevel(
    val maxInclusive: Int,
    val minInclusive: Int,
    val type: String,
) {
    fun toNBT(): CompoundBinaryTag {
        return nbt {
            withInt("max_inclusive", maxInclusive)
            withInt("min_inclusive", minInclusive)
            withString("type", type)
        }
    }
}