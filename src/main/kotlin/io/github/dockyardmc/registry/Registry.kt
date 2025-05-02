package io.github.dockyardmc.registry

import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.packets.configurations.ClientboundRegistryDataPacket
import io.netty.buffer.ByteBuf
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import java.io.InputStream

interface Registry {

    val identifier: String

    operator fun get(identifier: String): RegistryEntry
    fun getOrNull(identifier: String): RegistryEntry?

    fun getByProtocolId(id: Int): RegistryEntry

    fun getMap(): Map<String, RegistryEntry>

    fun getMaxProtocolId(): Int
}

interface DynamicRegistry : Registry {
    fun getCachedPacket(): ClientboundRegistryDataPacket
    fun updateCache()
    fun register()
}

interface DataDrivenRegistry : Registry {
    fun initialize(inputStream: InputStream)
}

interface DynamicDataDrivenRegistry : DataDrivenRegistry, DynamicRegistry

interface RegistryEntry : NetworkWritable {
    fun getNbt(): NBTCompound?
    fun getProtocolId(): Int
    fun getEntryIdentifier(): String

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(getProtocolId())
    }

    companion object {
        fun <T : RegistryEntry> read(buffer: ByteBuf, registry: Registry): T {
            @Suppress("UNCHECKED_CAST")
            return registry.getByProtocolId(buffer.readVarInt()) as T
        }
    }
}