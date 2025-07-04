package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.nbt.nbt
import io.github.dockyardmc.protocol.NbtWritable
import io.github.dockyardmc.registry.DataDrivenRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.sounds.CustomSoundEvent
import kotlinx.serialization.Serializable
import net.kyori.adventure.nbt.BinaryTagTypes
import net.kyori.adventure.nbt.CompoundBinaryTag

object BiomeRegistry : DataDrivenRegistry<Biome>() {
    override val identifier: String = "minecraft:worldgen/biome"
}

@Serializable
data class MoodSound(
    val blockSearchExtent: Int,
    val soundPositionOffset: Double,
    val sound: String,
    val tickDelay: Int,
) {
    fun toNBT(): CompoundBinaryTag {
        return nbt {
            withInt("block_search_extent", blockSearchExtent)
            withDouble("offset", soundPositionOffset)
            withString("sound", sound)
            withInt("tick_delay", tickDelay)
        }
    }
}

@Serializable
data class BackgroundMusic(
    val maxDelay: Int,
    val minDelay: Int,
    val replaceCurrentMusic: Boolean,
    val sound: String,
) {
    fun toNBT(): CompoundBinaryTag {
        return nbt {
            withCompound("sound", CustomSoundEvent(sound, null).getNbt())
            withInt("max_delay", maxDelay)
            withInt("min_delay", minDelay)
            withBoolean("replace_current_music", replaceCurrentMusic)
        }
    }
}

@Serializable
data class AmbientAdditions(
    val sound: String,
    val tickChance: Double,
) {
    fun toNBT(): CompoundBinaryTag {
        return nbt {
            withString("sound", sound)
            withDouble("tick_chance", tickChance)
        }
    }
}

@Serializable
data class BiomeParticles(
    val options: ParticleOptions,
    val probability: Float,
) {
    fun toNBT(): CompoundBinaryTag {
        return nbt {
            withCompound("options") {
                withString("type", options.type)
            }
            withFloat("probability", probability)
        }
    }
}

@Serializable
data class ParticleOptions(
    val type: String,
)

@Serializable
data class Effects(
    val fogColor: Int? = null,
    val foliageColor: Int? = null,
    val grassColor: Int? = null,
    val grassColorModifier: String? = null,
    val moodSound: MoodSound? = null,
    val music: List<Biome.WeightedBackgroundMusic>? = null,
    val musicVolume: Float? = null,
    val ambientAdditions: AmbientAdditions? = null,
    val ambientLoop: String? = null,
    val particle: BiomeParticles? = null,
    val skyColor: Int,
    val waterColor: Int,
    val waterFogColor: Int,
) {
    fun toNBT(): CompoundBinaryTag {
        return nbt {
            if (fogColor != null) withInt("fog_color", fogColor)
            if (foliageColor != null) withInt("foliage_color", foliageColor)
            if (grassColor != null) withInt("grass_color", grassColor)
            if (grassColorModifier != null) withString("grass_color_modifier", grassColorModifier)
            if (moodSound != null) withCompound("mood_sound", moodSound.toNBT())
            if (music != null) withList("music", BinaryTagTypes.COMPOUND, music.map { music -> music.getNbt() })
            if (musicVolume != null) withFloat("music_volume", musicVolume)
            if (ambientAdditions != null) withCompound("additions_sound", ambientAdditions.toNBT())
            if (particle != null) withCompound("particle", particle.toNBT())
            if (ambientLoop != null) withString("ambient_sound", ambientLoop)
            withInt("sky_color", skyColor)
            withInt("water_color", waterColor)
            withInt("water_fog_color", waterFogColor)
        }
    }
}

@Serializable
data class Biome(
    var identifier: String,
    val downfall: Float = 1f,
    var effects: Effects,
    val hasRain: Boolean = false,
    val temperature: Float = 1f,
    val temperatureModifier: String? = null,
) : RegistryEntry {

    override fun getProtocolId(): Int {
        return BiomeRegistry.getProtocolIdByEntry(this)
    }

    override fun getEntryIdentifier(): String {
        return identifier
    }

    override fun getNbt(): CompoundBinaryTag {
        return nbt {
            withFloat("downfall", downfall)
            withCompound("effects", effects.toNBT())
            withBoolean("has_precipitation", hasRain)
            withFloat("temperature", temperature)
            if (temperatureModifier != null) withString("temperature_modifier", temperatureModifier)
        }
    }

    @Serializable
    data class WeightedBackgroundMusic(val music: BackgroundMusic, val weight: Int) : NbtWritable {

        override fun getNbt(): CompoundBinaryTag {
            return nbt {
                withCompound("data", music.toNBT())
                withInt("weight", weight)
            }
        }

    }
}


