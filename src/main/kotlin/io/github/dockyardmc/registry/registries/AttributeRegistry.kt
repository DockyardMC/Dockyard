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
object AttributeRegistry : DataDrivenRegistry {

    val attributes: MutableMap<String, Attribute> = mutableMapOf()
    val protocolIdMap: Int2ObjectOpenHashMap<Attribute> = Int2ObjectOpenHashMap()
    val protocolIdMapReversed: Object2IntOpenHashMap<Attribute> = Object2IntOpenHashMap()
    private val protocolIdCounter = AtomicInteger()

    override val identifier: String = "minecraft:attribute"

    override fun initialize(inputStream: InputStream) {
        val stream = GZIPInputStream(inputStream)
        val list = Json.decodeFromStream<List<Attribute>>(stream)

        list.forEach { attribute ->
            val protocolId = protocolIdCounter.getAndIncrement()
            attributes[attribute.identifier] = attribute
            protocolIdMap[protocolId] = attribute
            protocolIdMapReversed[attribute] = protocolId
        }
    }

    override fun get(identifier: String): Attribute {
        return attributes[identifier] ?: throw RegistryException(identifier, attributes.size)
    }

    override fun getOrNull(identifier: String): Attribute? {
        return attributes[identifier]
    }

    override fun getByProtocolId(id: Int): Attribute {
        return protocolIdMap[id] ?: throw RegistryException(identifier, attributes.size)
    }

    override fun getMap(): Map<String, Attribute> {
        return attributes
    }

    override fun getMaxProtocolId(): Int {
        return protocolIdCounter.get()
    }
}

@Serializable
data class Attribute(
    val identifier: String,
    val translationKey: String,
    val defaultValue: Double,
    val clientSync: Boolean,
    val minValue: Double? = null,
    val maxValue: Double? = null
): RegistryEntry {

    override fun getNbt(): NBTCompound? {
        return null
    }

    override fun getProtocolId(): Int {
        return AttributeRegistry.protocolIdMapReversed.getOrThrow(this)
    }
}