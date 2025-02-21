package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.extentions.getOrThrow
import io.github.dockyardmc.protocol.packets.configurations.ClientboundRegistryDataPacket
import io.github.dockyardmc.registry.DataDrivenRegistry
import io.github.dockyardmc.registry.DynamicRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.registry.RegistryException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import java.io.InputStream
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.GZIPInputStream

object FluidRegistry: DataDrivenRegistry, DynamicRegistry {

    val fluids: MutableMap<String, Fluid> = mutableMapOf()
    val protocolIds: MutableMap<String, Int> = mutableMapOf()
    val protocolIdsReversed: MutableMap<Int, Fluid> = mutableMapOf()
    val protocolIdCounter = AtomicInteger(0)

    override val identifier: String = "minecraft:fluid"

    lateinit var packet: ClientboundRegistryDataPacket

    @OptIn(ExperimentalSerializationApi::class)
    override fun initialize(inputStream: InputStream) {
        val stream = GZIPInputStream(inputStream)
        val list = Json.decodeFromStream<List<Fluid>>(stream)
        list.forEach { entry -> addEntry(entry, false) }
        updateCache()
    }

    fun addEntry(entry: Fluid, updateCache: Boolean = true) {
        val protocolId = protocolIdCounter.getAndIncrement()
        protocolIds[entry.identifier] = protocolId
        protocolIdsReversed[protocolId] = entry
        fluids[entry.identifier] = entry
        if(updateCache) updateCache()
    }

    override fun getCachedPacket(): ClientboundRegistryDataPacket {
        if(!::packet.isInitialized) updateCache()
        return packet
    }

    override fun updateCache() {
        packet = ClientboundRegistryDataPacket(this)
    }

    override fun register() {}

    override fun get(identifier: String): Fluid {
        return fluids[identifier] ?: throw RegistryException(identifier, fluids.size)
    }

    override fun getOrNull(identifier: String): Fluid? {
        return fluids[identifier]
    }

    override fun getByProtocolId(id: Int): Fluid {
        return protocolIdsReversed[id] ?: throw RegistryException(id, fluids.size)
    }

    override fun getMap(): Map<String, Fluid> {
        return fluids
    }

    override fun getMaxProtocolId(): Int {
        return protocolIdCounter.get()
    }
}

@Serializable
data class Fluid(
    val identifier: String,
    val dripParticle: String?,
    val pickupSound: String,
    val explosionResistance: Float,
    val block: String
): RegistryEntry {

    override fun getNbt(): NBTCompound? = null

    override fun getProtocolId(): Int {
        return FluidRegistry.protocolIds.getOrThrow(identifier)
    }
}