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

object WolfVariantRegistry: DynamicRegistry {

    override val identifier: String = "minecraft:wolf_variant"

    private lateinit var cachedPacket: ClientboundRegistryDataPacket

    override fun getMaxProtocolId(): Int {
        return protocolIdCounter.get()
    }

    val wolfVariants: MutableMap<String, WolfVariant> = mutableMapOf()
    val protocolIds: MutableMap<String, Int> = mutableMapOf()
    private val protocolIdCounter = AtomicInteger()

    fun addEntry(entry: WolfVariant, updateCache: Boolean = true) {
        protocolIds[entry.identifier] = protocolIdCounter.getAndIncrement()
        wolfVariants[entry.identifier] = entry
        if (updateCache) updateCache()
    }

    override fun register() {
        addEntry(WolfVariant("minecraft:ashen", wildTexture = "minecraft:entity/wolf/wolf_ashen", angryTexture = "minecraft:entity/wolf/wolf_ashen_angry", biomes = "minecraft:snowy_taiga", tameTexture = "minecraft:entity/wolf/wolf_ashen_tame"))
        addEntry(WolfVariant("minecraft:black", wildTexture = "minecraft:entity/wolf/wolf_black", angryTexture = "minecraft:entity/wolf/wolf_black_angry", biomes = "minecraft:old_growth_pine_taiga", tameTexture = "minecraft:entity/wolf/wolf_black_tame"))
        addEntry(WolfVariant("minecraft:chestnut", wildTexture = "minecraft:entity/wolf/wolf_chestnut", angryTexture = "minecraft:entity/wolf/wolf_chestnut_angry", biomes = "minecraft:old_growth_spruce_taiga", tameTexture = "minecraft:entity/wolf/wolf_chestnut_tame"))
        addEntry(WolfVariant("minecraft:pale", wildTexture = "minecraft:entity/wolf/wolf", angryTexture = "minecraft:entity/wolf/wolf_angry", biomes = "minecraft:taiga", tameTexture = "minecraft:entity/wolf/wolf_tame"))
        addEntry(WolfVariant("minecraft:rusty", wildTexture = "minecraft:entity/wolf/wolf_rusty", angryTexture = "minecraft:entity/wolf/wolf_rusty_angry", biomes = "#minecraft:is_jungle", tameTexture = "minecraft:entity/wolf/wolf_rusty_tame"))
        addEntry(WolfVariant("minecraft:snowy", wildTexture = "minecraft:entity/wolf/wolf_snowy", angryTexture = "minecraft:entity/wolf/wolf_snowy_angry", biomes = "minecraft:grove", tameTexture = "minecraft:entity/wolf/wolf_snowy_tame"))
        addEntry(WolfVariant("minecraft:spotted", wildTexture = "minecraft:entity/wolf/wolf_spotted", angryTexture = "minecraft:entity/wolf/wolf_spotted_angry", biomes = "#minecraft:is_savanna", tameTexture = "minecraft:entity/wolf/wolf_spotted_tame"))
        addEntry(WolfVariant("minecraft:striped", wildTexture = "minecraft:entity/wolf/wolf_striped", angryTexture = "minecraft:entity/wolf/wolf_striped_angry", biomes = "#minecraft:is_badlands", tameTexture = "minecraft:entity/wolf/wolf_striped_tame"))
        addEntry(WolfVariant("minecraft:woods", wildTexture = "minecraft:entity/wolf/wolf_woods", angryTexture = "minecraft:entity/wolf/wolf_woods_angry", biomes = "minecraft:forest", tameTexture = "minecraft:entity/wolf/wolf_woods_tame"))
    }

    override fun getCachedPacket(): ClientboundRegistryDataPacket {
        if(!WolfVariantRegistry::cachedPacket.isInitialized) updateCache()
        return cachedPacket
    }

    override fun updateCache() {
        cachedPacket = ClientboundRegistryDataPacket(this)
    }

    override fun get(identifier: String): WolfVariant {
        return wolfVariants[identifier] ?: throw RegistryException(identifier, this.getMap().size)
    }

    override fun getOrNull(identifier: String): WolfVariant? {
        return wolfVariants[identifier]
    }

    override fun getByProtocolId(id: Int): WolfVariant {
        return wolfVariants.values.toList().getOrNull(id) ?: throw RegistryException(id, this.getMap().size)
    }

    override fun getMap(): Map<String, WolfVariant> {
        return wolfVariants
    }
}

data class WolfVariant(
    val identifier: String,
    val wildTexture: String,
    val angryTexture: String,
    val biomes: String,
    val tameTexture: String,
): RegistryEntry {

    override fun getProtocolId(): Int {
        return WolfVariantRegistry.protocolIds.getOrThrow(identifier)
    }

    override fun getNbt(): NBTCompound {
        return NBT.Compound {
            it.put("wild_texture", wildTexture)
            it.put("angry_texture", angryTexture)
            it.put("biomes", biomes)
            it.put("tame_texture", tameTexture)
        }
    }
}