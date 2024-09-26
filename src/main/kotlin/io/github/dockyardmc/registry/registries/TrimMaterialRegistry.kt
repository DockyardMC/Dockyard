package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.protocol.packets.configurations.ClientboundRegistryDataPacket
import io.github.dockyardmc.registry.DynamicRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import java.util.concurrent.atomic.AtomicInteger

object TrimMaterialRegistry: DynamicRegistry {

    override val identifier: String = "minecraft:trim_material"

    private lateinit var cachedPacket: ClientboundRegistryDataPacket

    val map: MutableMap<String, TrimMaterial> = mutableMapOf()
    val protocolIdCounter =  AtomicInteger()

    init {
        map["minecraft:amethyst"] = TrimMaterial("amethyst", "#9A5CC6", "trim_material.minecraft.amethyst", "minecraft:amethyst_shard", 1.0f, protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:copper"] = TrimMaterial("copper", "#B4684D", "trim_material.minecraft.copper", "minecraft:copper_ingot", 0.5f, protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:diamond"] = TrimMaterial("diamond", "#6EECD2", "trim_material.minecraft.diamond", "minecraft:diamond", 0.8f, mapOf("minecraft:diamond" to "diamond_darker"), protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:emerald"] = TrimMaterial("emerald", "#11A036", "trim_material.minecraft.emerald", "minecraft:emerald", 0.7f, protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:gold"] = TrimMaterial("gold", "#DEB12D", "trim_material.minecraft.gold", "minecraft:gold_ingot", 0.6f, mapOf("minecraft:gold" to "gold_darker"), protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:iron"] = TrimMaterial("iron", "#ECECEC", "trim_material.minecraft.iron", "minecraft:iron_ingot", 0.2f, mapOf("minecraft:iron" to "iron_darker"), protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:lapis"] = TrimMaterial("lapis", "#416E97", "trim_material.minecraft.lapis", "minecraft:lapis_lazuli", 0.9f, protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:netherite"] = TrimMaterial("netherite", "#625859", "trim_material.minecraft.netherite", "minecraft:netherite_ingot", 0.3f,mapOf("minecraft:netherite" to "netherite_darker"), protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:quartz"] = TrimMaterial("quartz", "#E3D4C4", "trim_material.minecraft.quartz", "minecraft:quartz", 0.1f, protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:redstone"] = TrimMaterial("redstone", "#971607", "trim_material.minecraft.redstone", "minecraft:redstone", 0.4f, protocolId = protocolIdCounter.getAndIncrement())
    }

    override fun getCachedPacket(): ClientboundRegistryDataPacket {
        if(!::cachedPacket.isInitialized) updateCache()
        return cachedPacket
    }

    override fun updateCache() {
        cachedPacket = ClientboundRegistryDataPacket(this)
    }

    override fun get(identifier: String): TrimMaterial {
        return map[identifier] ?: throw IllegalStateException("There is no registry entry with identifier $identifier")
    }

    override fun getOrNull(identifier: String): TrimMaterial? {
        return map[identifier]
    }

    override fun getByProtocolId(id: Int): TrimMaterial {
        return map.values.toList().getOrNull(id) ?: throw IllegalStateException("There is no registry entry with protocol id $id")
    }

    override fun getMap(): Map<String, TrimMaterial> {
        return map
    }
}

data class TrimMaterial(
    val assetName: String,
    val color: String,
    val translate: String,
    val ingredient: String,
    val itemModelIndex: Float,
    val overrideArmorMaterials: Map<String, String>? = null,
    override val protocolId: Int
): RegistryEntry {

    override fun getNbt(): NBTCompound {
        return NBT.Compound {
            it.put("asset_name", assetName)
            it.put("description", NBT.Compound { desc ->
                desc.put("color", color)
                desc.put("translate", translate)
            })
            it.put("ingredient", ingredient)
            it.put("item_model_index", itemModelIndex)
        }
    }
}