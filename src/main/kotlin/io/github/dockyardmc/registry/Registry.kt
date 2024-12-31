package io.github.dockyardmc.registry

import io.github.dockyardmc.protocol.packets.configurations.ClientboundRegistryDataPacket
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

interface DynamicRegistry: Registry {
    fun getCachedPacket(): ClientboundRegistryDataPacket
    fun updateCache()
    fun register()
}

interface DataDrivenRegistry: Registry {
    fun initialize(inputStream: InputStream)
}

interface DynamicDataDrivenRegistry: DataDrivenRegistry, DynamicRegistry {
}

interface RegistryEntry {
    fun getNbt(): NBTCompound?
    fun getProtocolId(): Int
}