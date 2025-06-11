package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.extentions.getOrThrow
import io.github.dockyardmc.nbt.nbt
import io.github.dockyardmc.protocol.packets.configurations.ClientboundRegistryDataPacket
import io.github.dockyardmc.registry.DataDrivenRegistry
import io.github.dockyardmc.registry.DynamicRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.registry.RegistryException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import net.kyori.adventure.nbt.CompoundBinaryTag
import java.io.InputStream
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.GZIPInputStream

@OptIn(ExperimentalSerializationApi::class)
object CatVariantRegistry : DataDrivenRegistry, DynamicRegistry {

    override val identifier: String = "minecraft:cat_variant"

    private lateinit var cachedPacket: ClientboundRegistryDataPacket
    val catVariants: MutableMap<String, CatVariant> = mutableMapOf()
    val protocolIds: MutableMap<String, Int> = mutableMapOf()
    private val protocolIdCounter = AtomicInteger()

    override fun getMaxProtocolId(): Int {
        return protocolIdCounter.get()
    }

    override fun initialize(inputStream: InputStream) {
        val stream = GZIPInputStream(inputStream)
        val list = Json.decodeFromStream<List<CatVariant>>(stream)
        list.forEach { entry -> addEntry(entry, false) }
        updateCache()
    }

    fun addEntry(entry: CatVariant, updateCache: Boolean = true) {
        protocolIds[entry.identifier] = protocolIdCounter.getAndIncrement()
        catVariants[entry.identifier] = entry
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

    override fun get(identifier: String): CatVariant {
        return catVariants[identifier] ?: throw RegistryException(identifier, this.getMap().size)
    }

    override fun getOrNull(identifier: String): CatVariant? {
        return catVariants[identifier]
    }

    override fun getByProtocolId(id: Int): CatVariant {
        return catVariants.values.toList().getOrNull(id) ?: throw RegistryException(id, this.getMap().size)
    }

    override fun getMap(): Map<String, CatVariant> {
        return catVariants
    }
}

@Serializable
data class CatVariant(
    val identifier: String,
    val assetId: String,
) : RegistryEntry {

    override fun getEntryIdentifier(): String {
        return identifier
    }

    override fun getProtocolId(): Int {
        return CatVariantRegistry.protocolIds.getOrThrow(identifier)
    }

    override fun getNbt(): CompoundBinaryTag {
        return nbt {
            withString("asset_id", assetId)
        }
    }
}