package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.extentions.getOrThrow
import io.github.dockyardmc.protocol.packets.configurations.ClientboundRegistryDataPacket
import io.github.dockyardmc.registry.DynamicRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.registry.RegistryException
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import java.util.concurrent.atomic.AtomicInteger

object PaintingVariantRegistry: DynamicRegistry {

    override val identifier: String = "minecraft:painting_variant"

    private lateinit var cachedPacket: ClientboundRegistryDataPacket

    val paintingVariants: MutableMap<String, PaintingVariant> = mutableMapOf()
    val protocolIds: MutableMap<String, Int> = mutableMapOf()
    private val protocolIdCounter = AtomicInteger()

    override fun getMaxProtocolId(): Int {
        return protocolIdCounter.get()
    }

    fun addEntry(entry: PaintingVariant, updateCache: Boolean = true) {
        protocolIds[entry.identifier] = protocolIdCounter.getAndIncrement()
        paintingVariants[entry.identifier] = entry
        if (updateCache) updateCache()
    }

    override fun register() {
        addEntry(PaintingVariant("minecraft:kebab", "kebab", 1, 1), false)
        addEntry(PaintingVariant("minecraft:aztec", "aztec", 1, 1), false)
        addEntry(PaintingVariant("minecraft:alban", "alban", 1, 1), false)
        addEntry(PaintingVariant("minecraft:aztec2", "aztec2", 1, 1), false)
        addEntry(PaintingVariant("minecraft:bomb", "bomb", 1, 1), false)
        addEntry(PaintingVariant("minecraft:plant", "plant", 1, 1), false)
        addEntry(PaintingVariant("minecraft:wasteland", "wasteland", 1, 1), false)
        addEntry(PaintingVariant("minecraft:pool", "pool", 1, 2), false)
        addEntry(PaintingVariant("minecraft:courbet", "courbet", 1, 2), false)
        addEntry(PaintingVariant("minecraft:sea", "sea", 1, 2), false)
        addEntry(PaintingVariant("minecraft:sunset", "sunset", 1, 2), false)
        addEntry(PaintingVariant("minecraft:creebet", "creebet", 1, 2), false)
        addEntry(PaintingVariant("minecraft:wanderer", "wanderer", 2, 1), false)
        addEntry(PaintingVariant("minecraft:graham", "graham", 2, 1), false)
        addEntry(PaintingVariant("minecraft:match", "match", 2, 2), false)
        addEntry(PaintingVariant("minecraft:bust", "bust", 2, 2), false)
        addEntry(PaintingVariant("minecraft:stage", "stage", 2, 2), false)
        addEntry(PaintingVariant("minecraft:void", "void", 2, 2), false)
        addEntry(PaintingVariant("minecraft:skull_and_roses", "skull_and_roses", 2, 2), false)
        addEntry(PaintingVariant("minecraft:wither", "wither", 2, 2), false)
        addEntry(PaintingVariant("minecraft:fighters", "fighters", 2, 4), false)
        addEntry(PaintingVariant("minecraft:pointer", "pointer", 4, 4), false)
        addEntry(PaintingVariant("minecraft:pigscene", "pigscene", 4, 4), false)
        addEntry(PaintingVariant("minecraft:burning_skull", "burning_skull", 4, 4), false)
        addEntry(PaintingVariant("minecraft:skeleton", "skeleton", 3, 4), false)
        addEntry(PaintingVariant("minecraft:earth", "earth", 2, 2), false)
        addEntry(PaintingVariant("minecraft:wind", "wind", 2, 2), false)
        addEntry(PaintingVariant("minecraft:water", "water", 2, 2), false)
        addEntry(PaintingVariant("minecraft:fire", "fire", 2, 2), false)
        addEntry(PaintingVariant("minecraft:donkey_kong", "donkey_kong", 3, 4), false)
        updateCache()
    }

    override fun getCachedPacket(): ClientboundRegistryDataPacket {
        if(!::cachedPacket.isInitialized) updateCache()
        return cachedPacket
    }

    override fun updateCache() {
        cachedPacket = ClientboundRegistryDataPacket(this)
    }

    override fun get(identifier: String): PaintingVariant {
        return paintingVariants[identifier] ?: throw RegistryException(identifier, this.getMap().size)
    }

    override fun getOrNull(identifier: String): PaintingVariant? {
        return paintingVariants[identifier]
    }

    override fun getByProtocolId(id: Int): PaintingVariant {
        return paintingVariants.values.toList().getOrNull(id) ?: throw RegistryException(id, this.getMap().size)
    }

    override fun getMap(): Map<String, PaintingVariant> {
        return paintingVariants
    }
}

data class PaintingVariant(
    val identifier: String,
    val assetId: String,
    val height: Int,
    val width: Int,
): RegistryEntry {

    override fun getProtocolId(): Int {
        return PaintingVariantRegistry.protocolIds.getOrThrow(identifier)
    }

    override fun getEntryIdentifier(): String {
        return identifier
    }


    override fun getNbt(): NBTCompound {
        return NBT.Compound {
            it.put("asset_id", assetId)
            it.put("height", height)
            it.put("width", width)
        }
    }
}