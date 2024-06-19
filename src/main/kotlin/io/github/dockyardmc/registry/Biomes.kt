package io.github.dockyardmc.registry

import cz.lukynka.prettylog.log
import io.github.dockyardmc.scroll.extensions.put
import io.github.dockyardmc.utils.Resources
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import org.jglrxavpok.hephaistos.parser.SNBTParser
import java.io.StringReader

object Biomes {

    val map by lazy {
        val vanillaEntry = Resources.getFile("vanillaregistry/worldgen_biome.snbt").split("\n")
        val list = mutableListOf<Biome>()
        vanillaEntry.forEach { pattern ->
            val split = pattern.split(";")
            val identifier = split[0]
            val sNBT = split[1]

            val nbt = (SNBTParser(StringReader(sNBT))).parse() as NBTCompound
            list.add(Biome.read(identifier, nbt))
        }
        list.associateBy { it.identifier }
    }

    fun getNewBiome(identifier: String): Biome {
        return map[identifier] ?: error("Biome with identifier $identifier not found")
    }

    fun getNewBiome(id: Int): Biome {
        return map.values.toList().getOrNull(id) ?: error("Biome with identifier $id not found")
    }

    val BADLANDS = getNewBiome("minecraft:badlands")
    val BAMBOO_JUNGLE = getNewBiome("minecraft:bamboo_jungle")
    val BASALT_DELTAS = getNewBiome("minecraft:basalt_deltas")
    val BEACH = getNewBiome("minecraft:beach")
    val BIRCH_FOREST = getNewBiome("minecraft:birch_forest")
    val CHERRY_GROVE = getNewBiome("minecraft:cherry_grove")
    val COLD_OCEAN = getNewBiome("minecraft:cold_ocean")
    val CRIMSON_FOREST = getNewBiome("minecraft:crimson_forest")
    val DARK_FOREST = getNewBiome("minecraft:dark_forest")
    val DEEP_COLD_OCEAN = getNewBiome("minecraft:deep_cold_ocean")
    val DEEP_DARK = getNewBiome("minecraft:deep_dark")
    val DEEP_FROZEN_OCEAN = getNewBiome("minecraft:deep_frozen_ocean")
    val DEEP_LUKEWARM_OCEAN = getNewBiome("minecraft:deep_lukewarm_ocean")
    val DEEP_OCEAN = getNewBiome("minecraft:deep_ocean")
    val DESERT = getNewBiome("minecraft:desert")
    val DRIPSTONE_CAVES = getNewBiome("minecraft:dripstone_caves")
    val END_BARRENS = getNewBiome("minecraft:end_barrens")
    val END_HIGHLANDS = getNewBiome("minecraft:end_highlands")
    val END_MIDLANDS = getNewBiome("minecraft:end_midlands")
    val ERODED_BADLANDS = getNewBiome("minecraft:eroded_badlands")
    val FLOWER_FOREST = getNewBiome("minecraft:flower_forest")
    val FOREST = getNewBiome("minecraft:forest")
    val FROZEN_OCEAN = getNewBiome("minecraft:frozen_ocean")
    val FROZEN_PEAKS = getNewBiome("minecraft:frozen_peaks")
    val FROZEN_RIVER = getNewBiome("minecraft:frozen_river")
    val GROVE = getNewBiome("minecraft:grove")
    val ICE_SPIKES = getNewBiome("minecraft:ice_spikes")
    val JAGGED_PEAKS = getNewBiome("minecraft:jagged_peaks")
    val JUNGLE = getNewBiome("minecraft:jungle")
    val LUKEWARM_OCEAN = getNewBiome("minecraft:lukewarm_ocean")
    val LUSH_CAVES = getNewBiome("minecraft:lush_caves")
    val MANGROVE_SWAMP = getNewBiome("minecraft:mangrove_swamp")
    val MEADOW = getNewBiome("minecraft:meadow")
    val MUSHROOM_FIELDS = getNewBiome("minecraft:mushroom_fields")
    val NETHER_WASTES = getNewBiome("minecraft:nether_wastes")
    val OCEAN = getNewBiome("minecraft:ocean")
    val OLD_GROWTH_BIRCH_FOREST = getNewBiome("minecraft:old_growth_birch_forest")
    val OLD_GROWTH_PINE_TAIGA = getNewBiome("minecraft:old_growth_pine_taiga")
    val OLD_GROWTH_SPRUCE_TAIGA = getNewBiome("minecraft:old_growth_spruce_taiga")
    val PLAINS = getNewBiome("minecraft:plains")
    val RIVER = getNewBiome("minecraft:river")
    val SAVANNA = getNewBiome("minecraft:savanna")
    val SAVANNA_PLATEAU = getNewBiome("minecraft:savanna_plateau")
    val SMALL_END_ISLANDS = getNewBiome("minecraft:small_end_islands")
    val SNOWY_BEACH = getNewBiome("minecraft:snowy_beach")
    val SNOWY_PLAINS = getNewBiome("minecraft:snowy_plains")
    val SNOWY_SLOPES = getNewBiome("minecraft:snowy_slopes")
    val SNOWY_TAIGA = getNewBiome("minecraft:snowy_taiga")
    val SOUL_SAND_VALLEY = getNewBiome("minecraft:soul_sand_valley")
    val SPARSE_JUNGLE = getNewBiome("minecraft:sparse_jungle")
    val STONY_PEAKS = getNewBiome("minecraft:stony_peaks")
    val STONY_SHORE = getNewBiome("minecraft:stony_shore")
    val SUNFLOWER_PLAINS = getNewBiome("minecraft:sunflower_plains")
    val SWAMP = getNewBiome("minecraft:swamp")
    val TAIGA = getNewBiome("minecraft:taiga")
    val THE_END = getNewBiome("minecraft:the_end")
    val THE_VOID = getNewBiome("minecraft:the_void")
    val WARM_OCEAN = getNewBiome("minecraft:warm_ocean")
    val WARPED_FOREST = getNewBiome("minecraft:warped_forest")
    val WINDSWEPT_FOREST = getNewBiome("minecraft:windswept_forest")
    val WINDSWEPT_GRAVELLY_HILLS = getNewBiome("minecraft:windswept_gravelly_hills")
    val WINDSWEPT_HILLS = getNewBiome("minecraft:windswept_hills")
    val WINDSWEPT_SAVANNA = getNewBiome("minecraft:windswept_savanna")
    val WOODED_BADLANDS = getNewBiome("minecraft:wooded_badlands")

    lateinit var registryCache: Registry

    fun cacheRegistry() {
        val entries = mutableListOf<RegistryEntry>()
        map.forEach {
            entries.add(RegistryEntry(it.key, it.value.toNBT()))
        }
        registryCache = Registry("minecraft:worldgen/biome", entries)
    }

    init {
        cacheRegistry()
    }
}

data class MoodSound(
    val blockSearchExtent: Int,
    val offset: Double,
    val sound: String,
    val tickDelay: Int
) {
    fun toNBT(): NBTCompound {
        return NBT.Compound {
            it.put("block_search_extent", blockSearchExtent)
            it.put("offset", offset)
            it.put("sound", sound)
            it.put("tick_delay", tickDelay)
        }
    }
}

data class Music(
    val maxDelay: Int,
    val minDelay: Int,
    val replaceCurrentMusic: Boolean,
    val sound: String
) {
    fun toNBT(): NBTCompound {
        return NBT.Compound {
            it.put("max_delay", maxDelay)
            it.put("min_delay", minDelay)
            it.put("replace_current_music", replaceCurrentMusic)
            it.put("sound", sound)
        }
    }
}

data class AdditionsSound(
    val sound: String,
    val tickChance: Double
) {
    fun toNBT(): NBTCompound {
        return NBT.Compound {
            it.put("sound", sound)
            it.put("tick_chance", tickChance)
        }
    }
}

data class BiomeParticle(
    val options: ParticleOptions,
    val probability: Float
) {
    fun toNBT(): NBTCompound {
        return NBT.Compound {
            it.put("options", NBT.Compound { oc ->
                oc.put("type", options.type)
            })
            it.put("probability", probability)
        }
    }
}

data class ParticleOptions(
    val type: String
)

data class Effects(
    val fogColor: Int?,
    val foliageColor: Int?,
    val grassColor: Int?,
    val grassColorModifier: String?,
    val moodSound: MoodSound,
    val music: Music?,
    val additionsSound: AdditionsSound?,
    val ambientSound: String?,
    val particle: BiomeParticle?,
    val skyColor: Int,
    val waterColor: Int,
    val waterFogColor: Int
) {
    fun toNBT(): NBTCompound {
        return NBT.Compound {
            it.put("fog_color", fogColor)
            it.put("foliage_color", foliageColor)
            it.put("grass_color", grassColor)
            if(grassColorModifier != null) it.put("grass_color_modifier", grassColorModifier)
            it.put("mood_sound", moodSound.toNBT())
            if(music != null) it.put("music", music.toNBT())
            if(additionsSound != null) it.put("additions_sound", additionsSound.toNBT())
            if(particle != null) it.put("particle", particle.toNBT())
            if(ambientSound != null) it.put("ambient_sound", ambientSound)
            it.put("sky_color", skyColor)
            it.put("water_color", waterColor)
            it.put("water_fog_color", waterFogColor)
        }
    }
}

data class Biome(
    val identifier: String,
    val downfall: Float,
    val effects: Effects,
    val hasPrecipitation: Boolean,
    val temperature: Float,
    val temperatureModifier: String?
) {

    fun toNBT(): NBTCompound {
        return NBT.Compound {
            it.put("downfall", downfall)
            it.put("effects", effects.toNBT())
            it.put("has_precipitation", hasPrecipitation)
            it.put("temperature", temperature)
            if(temperatureModifier != null) it.put("temperature_modifier", temperatureModifier)
        }
    }

    val id: Int get() = Biomes.map.values.indexOf(this)

    companion object {
        fun read(identifier: String, nbt: NBTCompound): Biome {
            val downfall = nbt.getFloat("downfall")!!
            val temperature = nbt.getFloat("temperature")!!
            val hasPrecipitation = nbt.getBoolean("has_precipitation")!!
            val temperatureModifier = nbt.getString("temperature_modifier")

            val effectsNBT = nbt.getCompound("effects")!!
            val fogColor = effectsNBT.getInt("fog_color")
            val foliageColor = effectsNBT.getInt("foliage_color")
            val grassColor = effectsNBT.getInt("grass_color")
            val skyColor = effectsNBT.getInt("sky_color")!!
            val waterColor = effectsNBT.getInt("water_color")!!
            val waterFogColor = effectsNBT.getInt("water_fog_color")!!

            val moodSoundNBT = effectsNBT.getCompound("mood_sound")!!
            val moodSound = MoodSound(
                blockSearchExtent = moodSoundNBT.getInt("block_search_extent")!!,
                offset = moodSoundNBT.getDouble("offset")!!,
                sound = moodSoundNBT.getString("sound")!!,
                tickDelay = moodSoundNBT.getInt("tick_delay")!!
            )

            val musicNBT = effectsNBT.getCompound("music")
            val music = if(musicNBT != null) Music(
                maxDelay = musicNBT.getInt("max_delay")!!,
                minDelay = musicNBT.getInt("min_delay")!!,
                replaceCurrentMusic = musicNBT.getBoolean("replace_current_music")!!,
                sound = musicNBT.getString("sound")!!
            ) else null

            val additionsSoundNBT = effectsNBT.getCompound("additions_sound")
            val additionsSound = if(additionsSoundNBT != null) AdditionsSound(
                additionsSoundNBT.getString("sound")!!,
                additionsSoundNBT.getAsDouble("tick_chance")!!
            ) else null

            val particleNBT = effectsNBT.getCompound("particle")
            val particle = if(particleNBT != null) BiomeParticle(
                options = ParticleOptions(
                    type = particleNBT.getCompound("options")!!.getString("type")!!
                ),
                probability = particleNBT.getFloat("probability")!!
            ) else null

            val grassColorModifier = effectsNBT.getString("grass_color_modifier")

            val ambientSound = effectsNBT.getString("ambient_sound")

            val effects = Effects(
                fogColor = fogColor,
                foliageColor = foliageColor,
                grassColorModifier = grassColorModifier,
                grassColor = grassColor,
                moodSound = moodSound,
                music = music,
                additionsSound = additionsSound,
                particle = particle,
                skyColor = skyColor,
                waterColor = waterColor,
                waterFogColor = waterFogColor,
                ambientSound = ambientSound
            )

            return Biome(
                identifier = identifier,
                downfall = downfall,
                effects = effects,
                hasPrecipitation = hasPrecipitation,
                temperature = temperature,
                temperatureModifier = temperatureModifier
            )
        }
    }
}