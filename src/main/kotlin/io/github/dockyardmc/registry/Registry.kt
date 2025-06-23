package io.github.dockyardmc.registry

import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.packets.configurations.ClientboundRegistryDataPacket
import io.github.dockyardmc.utils.BiMap
import io.github.dockyardmc.utils.MutableBiMap
import io.github.dockyardmc.utils.debug
import io.netty.buffer.ByteBuf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import net.kyori.adventure.nbt.BinaryTag
import java.io.InputStream
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.GZIPInputStream

abstract class Registry<T : RegistryEntry> {

    abstract val identifier: String
    protected var counter: AtomicInteger = AtomicInteger()
    protected val protocolEntries: MutableBiMap<Int, T> = MutableBiMap()
    protected val entries: MutableBiMap<String, T> = MutableBiMap()

    open fun addEntry(entry: T) {
        val id = counter.getAndIncrement()
        protocolEntries.put(id, entry)
        entries.put(entry.getEntryIdentifier(), entry)
    }

    operator fun get(identifier: String): T {
        return getOrNull(identifier) ?: throw RegistryException(identifier, entries.size)
    }

    fun getOrNull(identifier: String): T? {
        return entries.getByKeyOrNull(identifier)
    }

    fun getByProtocolId(id: Int): T {
        return getByProtocolIdOrNull(id) ?: throw RegistryException(id, protocolEntries.size)
    }

    fun getByProtocolIdOrNull(id: Int): T? {
        return protocolEntries.getByKeyOrNull(id)
    }

    fun getEntries(): BiMap<String, T> {
        return entries.toBiMap()
    }

    fun getProtocolEntries(): BiMap<Int, T> {
        return protocolEntries.toBiMap()
    }

    fun getMaxProtocolId(): Int {
        return entries.size
    }
}

abstract class DynamicRegistry<T : RegistryEntry> : Registry<T>() {
    protected lateinit var cachedPacket: ClientboundRegistryDataPacket

    @JvmName("getCachedPacketMethod")
    fun getCachedPacket(): ClientboundRegistryDataPacket {
        return cachedPacket
    }

    abstract fun updateCache()
}

@OptIn(ExperimentalSerializationApi::class)
abstract class DataDrivenRegistry<T : RegistryEntry> : Registry<T>() {

    inline fun <reified D : RegistryEntry> initialize(inputStream: InputStream) {
        val stream = GZIPInputStream(inputStream)
        val list = Json.decodeFromStream<List<D>>(stream)
        list.forEach { entry ->
            addEntry(entry as T)
        }
    }
}

interface RegistryEntry : NetworkWritable {
    fun getNbt(): BinaryTag? = null
    fun getProtocolId(): Int
    fun getEntryIdentifier(): String

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(getProtocolId())
    }

    companion object {
        fun <T : RegistryEntry> read(buffer: ByteBuf, registry: Registry<*>): T {
            @Suppress("UNCHECKED_CAST")
            return registry.getByProtocolId(buffer.readVarInt()) as T
        }
    }
}