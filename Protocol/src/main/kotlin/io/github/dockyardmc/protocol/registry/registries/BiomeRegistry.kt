package io.github.dockyardmc.protocol.registry.registries

import io.github.dockyardmc.common.getOrThrow
import io.github.dockyardmc.protocol.packets.configuration.clientbound.ClientboundRegistryDataPacket
import io.github.dockyardmc.protocol.registry.DataDrivenRegistry
import io.github.dockyardmc.protocol.registry.DynamicRegistry
import io.github.dockyardmc.protocol.registry.RegistryEntry
import io.github.dockyardmc.protocol.registry.RegistryException
import io.github.dockyardmc.scroll.extensions.put
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import java.io.InputStream
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.GZIPInputStream
import kotlin.reflect.KClass

object BiomeRegistry : DataDrivenRegistry, DynamicRegistry {

    override val identifier: String = "minecraft:worldgen/biome"

    val biomes: MutableMap<String, Biome> = mutableMapOf()
    val protocolIds: MutableMap<String, Int> = mutableMapOf()
    private val protocolIdCounter = AtomicInteger()

    lateinit var packet: ClientboundRegistryDataPacket

    override fun getEntryClass(): KClass<out RegistryEntry> {
        return Biome::class
    }

    override fun getMaxProtocolId(): Int {
        return protocolIdCounter.get()
    }

    override fun getCachedPacket(): ClientboundRegistryDataPacket {
        if (!BiomeRegistry::packet.isInitialized) updateCache()
        return packet
    }

    override fun updateCache() {
        packet = ClientboundRegistryDataPacket(this)
    }

    override fun register() {}

    fun addEntry(entry: Biome, updateCache: Boolean = true) {
        protocolIds[entry.identifier] = protocolIdCounter.getAndIncrement()
        biomes[entry.identifier] = entry
        if(updateCache) updateCache()
    }

    fun addEntries(vararg entries: Biome) {
        addEntries(entries.toList())
    }

    fun addEntries(entries: Collection<Biome>) {
        entries.forEach { addEntry(it, false) }
        updateCache()
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun initialize(inputStream: InputStream) {
        val stream = GZIPInputStream(inputStream)
        val list = Json.decodeFromStream<List<Biome>>(stream)
        addEntries(list)
    }

    override fun get(identifier: String): Biome {
        return biomes[identifier] ?: throw RegistryException(identifier, getMap().size)
    }

    override fun getOrNull(identifier: String): Biome? {
        return biomes[identifier]
    }

    override fun getByProtocolId(id: Int): Biome {
        return biomes.values.toList().getOrNull(id) ?: throw RegistryException(identifier, getMap().size)
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
    val tickDelay: Int,
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
    val sound: String,
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
data class AmbientAdditions(
    val sound: String,
    val tickChance: Double,
) {
    fun toNBT(): NBTCompound {
        return NBT.Compound {
            it.put("sound", sound)
            it.put("tick_chance", tickChance)
        }
    }
}

@Serializable
data class BiomeParticles(
    val options: ParticleOptions,
    val probability: Float,
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
    val type: String,
)

@Serializable
data class Effects(
    val fogColor: Int? = null,
    val foliageColor: Int? = null,
    val grassColor: Int? = null,
    val grassColorModifier: String? = null,
    val moodSound: MoodSound? = null,
    val music: BackgroundMusic? = null,
    val ambientAdditions: AmbientAdditions? = null,
    val ambientLoop: String? = null,
    val particle: BiomeParticles? = null,
    val skyColor: Int,
    val waterColor: Int,
    val waterFogColor: Int,
) {
    fun toNBT(): NBTCompound {
        return NBT.Compound {
            if (fogColor != null) it.put("fog_color", fogColor)
            if (foliageColor != null) it.put("foliage_color", foliageColor)
            if (grassColor != null) it.put("grass_color", grassColor)
            if (grassColorModifier != null) it.put("grass_color_modifier", grassColorModifier)
            if (moodSound != null) it.put("mood_sound", moodSound.toNBT())
            if (music != null) it.put("music", music.toNBT())
            if (ambientAdditions != null) it.put("additions_sound", ambientAdditions.toNBT())
            if (particle != null) it.put("particle", particle.toNBT())
            if (ambientLoop != null) it.put("ambient_sound", ambientLoop)
            it.put("sky_color", skyColor)
            it.put("water_color", waterColor)
            it.put("water_fog_color", waterFogColor)
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
) : RegistryEntry() {

    override fun getProtocolId(): Int {
        return BiomeRegistry.protocolIds.getOrThrow(identifier)
    }

    override fun getIdentifier(): String {
        return identifier
    }

    override fun getNbt(): NBTCompound {
        return NBT.Compound {
            it.put("downfall", downfall)
            it.put("effects", effects.toNBT())
            it.put("has_precipitation", hasRain)
            it.put("temperature", temperature)
            if (temperatureModifier != null) it.put("temperature_modifier", temperatureModifier)
        }
    }
}