package io.github.dockyardmc.registry

import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.packets.configurations.clientbound.ClientboundRegistryDataPacket
import io.github.dockyardmc.protocol.writeOptional
import io.github.dockyardmc.utils.BiMap
import io.github.dockyardmc.utils.MutableBiMap
import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import net.kyori.adventure.nbt.BinaryTag
import java.io.InputStream
import java.util.zip.GZIPInputStream

abstract class Registry<T : RegistryEntry> : NetworkWritable {

    abstract val identifier: String

    protected val protocolEntries: ObjectArrayList<T> = ObjectArrayList()
    protected val entryToProtocolId: Object2IntOpenHashMap<T> = Object2IntOpenHashMap()
    protected val entries: MutableBiMap<String, T> = MutableBiMap()

    open fun addEntry(entry: T) {
        val size = protocolEntries.size
        protocolEntries.add(entry)
        entries.put(entry.getEntryIdentifier(), entry)
        entryToProtocolId.put(entry, size)
    }

    operator fun get(identifier: String): T {
        return getOrNull(identifier) ?: throw RegistryException(identifier, entries.size)
    }

    fun getOrNull(identifier: String): T? {
        return entries.getByKeyOrNull(identifier)
    }

    fun getProtocolIdByEntry(entry: T): Int {
        return entryToProtocolId.getInt(entry)
    }

    open fun getByProtocolId(id: Int): T {
        return getByProtocolIdOrNull(id) ?: throw RegistryException(id, protocolEntries.size)
    }

    fun getByProtocolIdOrNull(id: Int): T? {
        return protocolEntries.getOrNull(id)
    }

    fun getEntries(): BiMap<String, T> {
        return entries.toBiMap()
    }

    fun getProtocolEntries(): List<T> {
        return protocolEntries.toList()
    }

    fun getMaxProtocolId(): Int {
        return entries.size
    }

    override fun write(buffer: ByteBuf) {
        val size = this.getMaxProtocolId()

        buffer.writeString(this.identifier)

        buffer.writeVarInt(size)
        this.protocolEntries.forEach { entry ->
            buffer.writeString(entry.getEntryIdentifier())
            buffer.writeOptional(entry.getNbt(), ByteBuf::writeNBT)
        }
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

@Suppress("UNCHECKED_CAST")
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