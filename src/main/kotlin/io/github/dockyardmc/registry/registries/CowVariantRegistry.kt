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
object CowVariantRegistry : DataDrivenRegistry, DynamicRegistry {

    override val identifier: String = "minecraft:cow_variant"

    private lateinit var cachedPacket: ClientboundRegistryDataPacket
    val cowVariants: MutableMap<String, CowVariant> = mutableMapOf()
    val protocolIds: MutableMap<String, Int> = mutableMapOf()
    private val protocolIdCounter = AtomicInteger()

    override fun getMaxProtocolId(): Int {
        return protocolIdCounter.get()
    }

    override fun initialize(inputStream: InputStream) {
        val stream = GZIPInputStream(inputStream)
        val list = Json.decodeFromStream<List<CowVariant>>(stream)
        list.forEach { entry -> addEntry(entry, false) }
        updateCache()
    }

    fun addEntry(entry: CowVariant, updateCache: Boolean = true) {
        protocolIds[entry.identifier] = protocolIdCounter.getAndIncrement()
        cowVariants[entry.identifier] = entry
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

    override fun get(identifier: String): CowVariant {
        return cowVariants[identifier] ?: throw RegistryException(identifier, this.getMap().size)
    }

    override fun getOrNull(identifier: String): CowVariant? {
        return cowVariants[identifier]
    }

    override fun getByProtocolId(id: Int): CowVariant {
        return cowVariants.values.toList().getOrNull(id) ?: throw RegistryException(id, this.getMap().size)
    }

    override fun getMap(): Map<String, CowVariant> {
        return cowVariants
    }
}

@Serializable
data class CowVariant(
    val identifier: String,
    val assetId: String,
) : RegistryEntry {

    override fun getProtocolId(): Int {
        return CowVariantRegistry.protocolIds.getOrThrow(identifier)
    }

    override fun getEntryIdentifier(): String {
        return identifier
    }


    override fun getNbt(): NBTCompound {
        return NBT.Compound { builder ->
            builder.put("asset_id", assetId)
        }
    }
}