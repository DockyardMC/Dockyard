package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.extentions.getOrThrow
import io.github.dockyardmc.registry.DataDrivenRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.registry.RegistryException
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import java.io.InputStream
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.GZIPInputStream

@OptIn(ExperimentalSerializationApi::class)
object PotionTypeRegistry : DataDrivenRegistry {

    override val identifier: String = "minecraft:potion"

    val potionTypes: MutableMap<String, PotionType> = mutableMapOf()
    val protocolIdMap: Int2ObjectOpenHashMap<PotionType> = Int2ObjectOpenHashMap()
    val protocolIdMapReversed: Object2IntOpenHashMap<PotionType> = Object2IntOpenHashMap()
    private val protocolIdCounter = AtomicInteger()

    override fun initialize(inputStream: InputStream) {
        val stream = GZIPInputStream(inputStream)
        val list = Json.decodeFromStream<List<PotionType>>(stream)

        list.forEach { potionType ->
            val protocolId = protocolIdCounter.getAndIncrement()
            potionTypes[potionType.identifier] = potionType
            protocolIdMap.put(protocolId, potionType)
            protocolIdMapReversed.put(potionType, protocolId)
        }
    }


    override fun get(identifier: String): PotionType {
        return getOrNull(identifier) ?: throw RegistryException(identifier, potionTypes.size)
    }

    override fun getOrNull(identifier: String): PotionType? {
        return potionTypes[identifier]
    }

    override fun getByProtocolId(id: Int): PotionType {
        return protocolIdMap.getOrThrow(id)
    }

    override fun getMap(): Map<String, PotionType> {
        return potionTypes
    }

    override fun getMaxProtocolId(): Int {
        return protocolIdCounter.get()
    }
}

@Serializable
data class PotionType(
    val identifier: String,
) : RegistryEntry {
    override fun getNbt(): NBTCompound {
        return NBTCompound.EMPTY
    }

    override fun getProtocolId(): Int {
        return PotionTypeRegistry.protocolIdMapReversed.getOrThrow(this)
    }

    override fun getEntryIdentifier(): String {
        return identifier
    }
}