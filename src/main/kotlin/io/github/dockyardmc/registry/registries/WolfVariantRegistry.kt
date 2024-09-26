package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.protocol.packets.configurations.ClientboundRegistryDataPacket
import io.github.dockyardmc.registry.DynamicRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import java.util.concurrent.atomic.AtomicInteger

object WolfVariantRegistry: DynamicRegistry {

    override val identifier: String = "minecraft:wolf_variant"

    private lateinit var cachedPacket: ClientboundRegistryDataPacket

    val wolfVariants: MutableMap<String, WolfVariant> = mutableMapOf()
    val protocolIdCounter =  AtomicInteger()

    init {
        wolfVariants["minecraft:ashen"] = WolfVariant(
            wildTexture = "minecraft:entity/wolf/wolf_ashen",
            angryTexture = "minecraft:entity/wolf/wolf_ashen_angry",
            biomes = "minecraft:snowy_taiga",
            tameTexture = "minecraft:entity/wolf/wolf_ashen_tame",
            protocolId = protocolIdCounter.getAndIncrement()
        )

        wolfVariants["minecraft:black"] = WolfVariant(
            wildTexture = "minecraft:entity/wolf/wolf_black",
            angryTexture = "minecraft:entity/wolf/wolf_black_angry",
            biomes = "minecraft:old_growth_pine_taiga",
            tameTexture = "minecraft:entity/wolf/wolf_black_tame",
            protocolId = protocolIdCounter.getAndIncrement()
        )
        wolfVariants["minecraft:chestnut"] = WolfVariant(
            wildTexture = "minecraft:entity/wolf/wolf_chestnut",
            angryTexture = "minecraft:entity/wolf/wolf_chestnut_angry",
            biomes = "minecraft:old_growth_spruce_taiga",
            tameTexture = "minecraft:entity/wolf/wolf_chestnut_tame",
            protocolId = protocolIdCounter.getAndIncrement()
        )
        wolfVariants["minecraft:pale"] = WolfVariant(
            wildTexture = "minecraft:entity/wolf/wolf",
            angryTexture = "minecraft:entity/wolf/wolf_angry",
            biomes = "minecraft:taiga",
            tameTexture = "minecraft:entity/wolf/wolf_tame",
            protocolId = protocolIdCounter.getAndIncrement()
        )
        wolfVariants["minecraft:rusty"] = WolfVariant(
            wildTexture = "minecraft:entity/wolf/wolf_rusty",
            angryTexture = "minecraft:entity/wolf/wolf_rusty_angry",
            biomes = "#minecraft:is_jungle",
            tameTexture = "minecraft:entity/wolf/wolf_rusty_tame",
            protocolId = protocolIdCounter.getAndIncrement()
        )
        wolfVariants["minecraft:snowy"] = WolfVariant(
            wildTexture = "minecraft:entity/wolf/wolf_snowy",
            angryTexture = "minecraft:entity/wolf/wolf_snowy_angry",
            biomes = "minecraft:grove",
            tameTexture = "minecraft:entity/wolf/wolf_snowy_tame",
            protocolId = protocolIdCounter.getAndIncrement()
        )
        wolfVariants["minecraft:spotted"] = WolfVariant(
            wildTexture = "minecraft:entity/wolf/wolf_spotted",
            angryTexture = "minecraft:entity/wolf/wolf_spotted_angry",
            biomes = "#minecraft:is_savanna",
            tameTexture = "minecraft:entity/wolf/wolf_spotted_tame",
            protocolId = protocolIdCounter.getAndIncrement()
        )
        wolfVariants["minecraft:striped"] = WolfVariant(
            wildTexture = "minecraft:entity/wolf/wolf_striped",
            angryTexture = "minecraft:entity/wolf/wolf_striped_angry",
            biomes = "#minecraft:is_badlands",
            tameTexture = "minecraft:entity/wolf/wolf_striped_tame",
            protocolId = protocolIdCounter.getAndIncrement()
        )
        wolfVariants["minecraft:woods"] to WolfVariant(
            wildTexture = "minecraft:entity/wolf/wolf_woods",
            angryTexture = "minecraft:entity/wolf/wolf_woods_angry",
            biomes = "minecraft:forest",
            tameTexture = "minecraft:entity/wolf/wolf_woods_tame",
            protocolId = protocolIdCounter.getAndIncrement()
        )
    }

    override fun getCachedPacket(): ClientboundRegistryDataPacket {
        if(!WolfVariantRegistry::cachedPacket.isInitialized) updateCache()
        return cachedPacket
    }

    override fun updateCache() {
        cachedPacket = ClientboundRegistryDataPacket(this)
    }

    override fun get(identifier: String): WolfVariant {
        return wolfVariants[identifier] ?: throw IllegalStateException("There is no registry entry with identifier $identifier")
    }

    override fun getOrNull(identifier: String): WolfVariant? {
        return wolfVariants[identifier]
    }

    override fun getByProtocolId(id: Int): WolfVariant {
        return wolfVariants.values.toList().getOrNull(id) ?: throw IllegalStateException("There is no registry entry with protocol id $id")
    }

    override fun getMap(): Map<String, WolfVariant> {
        return wolfVariants
    }
}

data class WolfVariant(
    val wildTexture: String,
    val angryTexture: String,
    val biomes: String,
    val tameTexture: String,
    override val protocolId: Int
): RegistryEntry {

    override fun getNbt(): NBTCompound {
        return NBT.Compound {
            it.put("wild_texture", wildTexture)
            it.put("angry_texture", angryTexture)
            it.put("biomes", biomes)
            it.put("tame_texture", tameTexture)
        }
    }
}