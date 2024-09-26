package io.github.dockyardmc.registry.registries

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.registry.DataDrivenRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.scroll.extensions.put
import io.github.dockyardmc.utils.debug
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import java.io.InputStream
import java.lang.IllegalStateException
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.GZIPInputStream

object BiomeRegistry: DataDrivenRegistry {
    override val identifier: String = "minecraft:biome"

    var biomes: MutableMap<String, Biome> = mutableMapOf()
    val protocolIdCounter =  AtomicInteger()

    @OptIn(ExperimentalSerializationApi::class)
    override fun initialize(inputStream: InputStream) {
        val stream = GZIPInputStream(inputStream)
        val list = Json.decodeFromStream<List<Biome>>(stream)
        biomes = list.associateBy { it.identifier }.toMutableMap()
        debug("Loaded biome registry: ${biomes.size} entries", false)
    }

    override fun get(identifier: String): Biome {
        return biomes[identifier] ?: throw IllegalStateException("Biome with identifier $identifier is not present in the registry!")
    }

    override fun getOrNull(identifier: String): Biome? {
        return biomes[identifier]
    }

    override fun getByProtocolId(id: Int): Biome {
        return biomes.values.toList().getOrNull(id) ?: throw IllegalStateException("There is no registry entry with protocol id $id")
    }

    override fun getMap(): Map<String, Biome> {
        return biomes
    }
}

@Serializable
data class MoodSound(
    val blockSearchExtent: Int,
    val soundPositionOffset: Double,
    val sound: String,
    val tickDelay: Int
) {
    fun toNBT(): NBTCompound {
        return NBT.Compound {
            it.put("block_search_extent", blockSearchExtent)
            it.put("offset", soundPositionOffset)
            it.put("sound", sound)
            it.put("tick_delay", tickDelay)
        }
    }
}

@Serializable
data class BackgroundMusic(
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

@Serializable
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

@Serializable
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

@Serializable
data class ParticleOptions(
    val type: String
)

@Serializable
data class Effects(
    val fogColor: Int? = null,
    val foliageColor: Int? = null,
    val moodSound: MoodSound? = null,
    val music: BackgroundMusic? = null,
    val ambientAdditions: AdditionsSound? = null,
    val ambientLoop: String? = null,
    val particle: BiomeParticle? = null,
    val skyColor: Int,
    val waterColor: Int,
    val waterFogColor: Int
) {
    fun toNBT(): NBTCompound {
        return NBT.Compound {
            it.put("fog_color", fogColor)
            it.put("foliage_color", foliageColor)
            if(moodSound != null) it.put("mood_sound", moodSound.toNBT())
            if(music != null) it.put("music", music.toNBT())
            if(ambientAdditions != null) it.put("additions_sound", ambientAdditions.toNBT())
            if(particle != null) it.put("particle", particle.toNBT())
            if(ambientLoop != null) it.put("ambient_sound", ambientLoop)
            it.put("sky_color", skyColor)
            it.put("water_color", waterColor)
            it.put("water_fog_color", waterFogColor)
        }
    }
}

@Serializable
data class Biome(
    var identifier: String,
    val downfall: String = "rain",
    var effects: Effects,
    val hasRain: Boolean = false,
    val temperature: Float = 1f,
    override val protocolId: Int,
): RegistryEntry {

    override fun getNbt(): NBTCompound {
        return NBT.Compound {
            it.put("downfall", downfall)
            it.put("effects", effects.toNBT())
            it.put("has_precipitation", hasRain)
            it.put("temperature", temperature)
        }
    }
}