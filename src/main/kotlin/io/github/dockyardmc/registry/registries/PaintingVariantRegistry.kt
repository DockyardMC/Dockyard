package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.protocol.packets.configurations.ClientboundRegistryDataPacket
import io.github.dockyardmc.registry.DynamicRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import java.util.concurrent.atomic.AtomicInteger

object PaintingVariantRegistry: DynamicRegistry {

    override val identifier: String = "minecraft:painting_variant"

    private lateinit var cachedPacket: ClientboundRegistryDataPacket

    val map: MutableMap<String, PaintingVariant> = mutableMapOf()
    val protocolIdCounter =  AtomicInteger()

    init {
        map["minecraft:kebab"] = PaintingVariant("kebab", 1, 1, protocolIdCounter.getAndIncrement())
        map["minecraft:aztec"] = PaintingVariant("aztec", 1, 1, protocolIdCounter.getAndIncrement())
        map["minecraft:alban"] = PaintingVariant("alban", 1, 1, protocolIdCounter.getAndIncrement())
        map["minecraft:aztec2"] = PaintingVariant("aztec2", 1, 1, protocolIdCounter.getAndIncrement())
        map["minecraft:bomb"] = PaintingVariant("bomb", 1, 1, protocolIdCounter.getAndIncrement())
        map["minecraft:plant"] = PaintingVariant("plant", 1, 1, protocolIdCounter.getAndIncrement())
        map["minecraft:wasteland"] = PaintingVariant("wasteland", 1, 1, protocolIdCounter.getAndIncrement())
        map["minecraft:pool"] = PaintingVariant("pool", 1, 2, protocolIdCounter.getAndIncrement())
        map["minecraft:courbet"] = PaintingVariant("courbet", 1, 2, protocolIdCounter.getAndIncrement())
        map["minecraft:sea"] = PaintingVariant("sea", 1, 2, protocolIdCounter.getAndIncrement())
        map["minecraft:sunset"] = PaintingVariant("sunset", 1, 2, protocolIdCounter.getAndIncrement())
        map["minecraft:creebet"] = PaintingVariant("creebet", 1, 2, protocolIdCounter.getAndIncrement())
        map["minecraft:wanderer"] = PaintingVariant("wanderer", 2, 1, protocolIdCounter.getAndIncrement())
        map["minecraft:graham"] = PaintingVariant("graham", 2, 1, protocolIdCounter.getAndIncrement())
        map["minecraft:match"] = PaintingVariant("match", 2, 2, protocolIdCounter.getAndIncrement())
        map["minecraft:bust"] = PaintingVariant("bust", 2, 2, protocolIdCounter.getAndIncrement())
        map["minecraft:stage"] = PaintingVariant("stage", 2, 2, protocolIdCounter.getAndIncrement())
        map["minecraft:void"] = PaintingVariant("void", 2, 2, protocolIdCounter.getAndIncrement())
        map["minecraft:skull_and_roses"] = PaintingVariant("skull_and_roses", 2, 2, protocolIdCounter.getAndIncrement())
        map["minecraft:wither"] = PaintingVariant("wither", 2, 2, protocolIdCounter.getAndIncrement())
        map["minecraft:fighters"] = PaintingVariant("fighters", 2, 4, protocolIdCounter.getAndIncrement())
        map["minecraft:pointer"] = PaintingVariant("pointer", 4, 4, protocolIdCounter.getAndIncrement())
        map["minecraft:pigscene"] = PaintingVariant("pigscene", 4, 4, protocolIdCounter.getAndIncrement())
        map["minecraft:burning_skull"] = PaintingVariant("burning_skull", 4, 4, protocolIdCounter.getAndIncrement())
        map["minecraft:skeleton"] = PaintingVariant("skeleton", 3, 4, protocolIdCounter.getAndIncrement())
        map["minecraft:earth"] = PaintingVariant("earth", 2, 2, protocolIdCounter.getAndIncrement())
        map["minecraft:wind"] = PaintingVariant("wind", 2, 2, protocolIdCounter.getAndIncrement())
        map["minecraft:water"] = PaintingVariant("water", 2, 2, protocolIdCounter.getAndIncrement())
        map["minecraft:fire"] = PaintingVariant("fire", 2, 2, protocolIdCounter.getAndIncrement())
        map["minecraft:donkey_kong"] = PaintingVariant("donkey_kong", 3, 4, protocolIdCounter.getAndIncrement())
    }

    override fun getCachedPacket(): ClientboundRegistryDataPacket {
        if(!::cachedPacket.isInitialized) updateCache()
        return cachedPacket
    }

    override fun updateCache() {
        cachedPacket = ClientboundRegistryDataPacket(this)
    }

    override fun get(identifier: String): PaintingVariant {
        return map[identifier] ?: throw IllegalStateException("There is no registry entry with identifier $identifier")
    }

    override fun getOrNull(identifier: String): PaintingVariant? {
        return map[identifier]
    }

    override fun getByProtocolId(id: Int): PaintingVariant {
        return map.values.toList().getOrNull(id) ?: throw IllegalStateException("There is no registry entry with protocol id $id")
    }

    override fun getMap(): Map<String, PaintingVariant> {
        return map
    }
}

data class PaintingVariant(val assetId: String, val height: Int, val width: Int, override val protocolId: Int): RegistryEntry {
    override fun getNbt(): NBTCompound {
        return NBT.Compound {
            it.put("asset_id", assetId)
            it.put("height", height)
            it.put("width", width)
        }
    }
}