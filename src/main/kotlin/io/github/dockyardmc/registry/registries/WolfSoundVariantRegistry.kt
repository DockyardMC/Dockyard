package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.extentions.getOrThrow
import io.github.dockyardmc.protocol.packets.configurations.ClientboundRegistryDataPacket
import io.github.dockyardmc.registry.DataDrivenRegistry
import io.github.dockyardmc.registry.DynamicRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.registry.RegistryException
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

@OptIn(ExperimentalSerializationApi::class)
object WolfSoundVariantRegistry : DataDrivenRegistry, DynamicRegistry {

    override val identifier: String = "minecraft:wolf_sound_variant"

    private lateinit var cachedPacket: ClientboundRegistryDataPacket
    val wolfSoundVariants: MutableMap<String, WolfSoundVariant> = mutableMapOf()
    val protocolIds: MutableMap<String, Int> = mutableMapOf()
    private val protocolIdCounter = AtomicInteger()

    override fun getMaxProtocolId(): Int {
        return protocolIdCounter.get()
    }

    override fun initialize(inputStream: InputStream) {
        val stream = GZIPInputStream(inputStream)
        val list = Json.decodeFromStream<List<WolfSoundVariant>>(stream)
        list.forEach { entry -> addEntry(entry, false) }
        updateCache()
    }

    fun addEntry(entry: WolfSoundVariant, updateCache: Boolean = true) {
        protocolIds[entry.identifier] = protocolIdCounter.getAndIncrement()
        wolfSoundVariants[entry.identifier] = entry
        if (updateCache) updateCache()
    }

    override fun register() {
    }

    override fun getCachedPacket(): ClientboundRegistryDataPacket {
        if (!::cachedPacket.isInitialized) updateCache()
        return cachedPacket
    }

    override fun updateCache() {
        cachedPacket = ClientboundRegistryDataPacket(this)
    }

    override fun get(identifier: String): WolfSoundVariant {
        return wolfSoundVariants[identifier] ?: throw RegistryException(identifier, this.getMap().size)
    }

    override fun getOrNull(identifier: String): WolfSoundVariant? {
        return wolfSoundVariants[identifier]
    }

    override fun getByProtocolId(id: Int): WolfSoundVariant {
        return wolfSoundVariants.values.toList().getOrNull(id) ?: throw RegistryException(id, this.getMap().size)
    }

    override fun getMap(): Map<String, WolfSoundVariant> {
        return wolfSoundVariants
    }
}

@Serializable
data class WolfSoundVariant(
    val identifier: String,
    val ambientSound: String,
    val deathSound: String,
    val growlSound: String,
    val hurtSound: String,
    val pantSound: String,
    val whineSound: String,
) : RegistryEntry {

    override fun getProtocolId(): Int {
        return WolfVariantRegistry.protocolIds.getOrThrow(identifier)
    }

    override fun getNbt(): NBTCompound {
        return NBT.Compound { builder ->
            builder.put("ambient_sound", ambientSound)
            builder.put("death_sound", deathSound)
            builder.put("growl_sound", growlSound)
            builder.put("hurt_sound", hurtSound)
            builder.put("pant_sound", pantSound)
            builder.put("whine_sound", whineSound)
        }
    }
}