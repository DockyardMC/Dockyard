package io.github.dockyardmc.protocol.registry.registries

import io.github.dockyardmc.common.getOrThrow
import io.github.dockyardmc.protocol.packets.configuration.clientbound.ClientboundRegistryDataPacket
import io.github.dockyardmc.protocol.registry.DynamicRegistry
import io.github.dockyardmc.protocol.registry.RegistryEntry
import io.github.dockyardmc.protocol.registry.RegistryException
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import java.util.concurrent.atomic.AtomicInteger

object TrimMaterialRegistry: DynamicRegistry {

    override val identifier: String = "minecraft:trim_material"

    private lateinit var cachedPacket: ClientboundRegistryDataPacket

    override fun getMaxProtocolId(): Int {
        return protocolIdCounter.get()
    }

    val trimMaterials: MutableMap<String, TrimMaterial> = mutableMapOf()
    val protocolIds: MutableMap<String, Int> = mutableMapOf()
    private val protocolIdCounter = AtomicInteger()

    fun addEntry(entry: TrimMaterial, updateCache: Boolean = true) {
        protocolIds[entry.identifier] = protocolIdCounter.getAndIncrement()
        trimMaterials[entry.identifier] = entry
        if (updateCache) updateCache()
    }

    override fun register() {
        addEntry(TrimMaterial("minecraft:amethyst", "amethyst", "#9A5CC6", "trim_material.minecraft.amethyst", "minecraft:amethyst_shard", 1.0f))
        addEntry(TrimMaterial("minecraft:copper", "copper", "#B4684D", "trim_material.minecraft.copper", "minecraft:copper_ingot", 0.5f))
        addEntry(TrimMaterial("minecraft:diamond", "diamond", "#6EECD2", "trim_material.minecraft.diamond", "minecraft:diamond", 0.8f, mapOf("minecraft:diamond" to "diamond_darker")))
        addEntry(TrimMaterial("minecraft:emerald", "emerald", "#11A036", "trim_material.minecraft.emerald", "minecraft:emerald", 0.7f))
        addEntry(TrimMaterial("minecraft:gold", "gold", "#DEB12D", "trim_material.minecraft.gold", "minecraft:gold_ingot", 0.6f, mapOf("minecraft:gold" to "gold_darker")))
        addEntry(TrimMaterial("minecraft:iron", "iron", "#ECECEC", "trim_material.minecraft.iron", "minecraft:iron_ingot", 0.2f, mapOf("minecraft:iron" to "iron_darker")))
        addEntry(TrimMaterial("minecraft:lapis", "lapis", "#416E97", "trim_material.minecraft.lapis", "minecraft:lapis_lazuli", 0.9f))
        addEntry(TrimMaterial("minecraft:netherite", "netherite", "#625859", "trim_material.minecraft.netherite", "minecraft:netherite_ingot", 0.3f,mapOf("minecraft:netherite" to "netherite_darker")))
        addEntry(TrimMaterial("minecraft:quartz", "quartz", "#E3D4C4", "trim_material.minecraft.quartz", "minecraft:quartz", 0.1f))
        addEntry(TrimMaterial("minecraft:redstone", "redstone", "#971607", "trim_material.minecraft.redstone", "minecraft:redstone", 0.4f))
        updateCache()
    }

    override fun getCachedPacket(): ClientboundRegistryDataPacket {
        if(!TrimMaterialRegistry::cachedPacket.isInitialized) updateCache()
        return cachedPacket
    }

    override fun updateCache() {
        cachedPacket = ClientboundRegistryDataPacket(this)
    }

    override fun get(identifier: String): TrimMaterial {
        return trimMaterials[identifier] ?: throw RegistryException(identifier, getMap().size)
    }

    override fun getOrNull(identifier: String): TrimMaterial? {
        return trimMaterials[identifier]
    }

    override fun getByProtocolId(id: Int): TrimMaterial {
        return trimMaterials.values.toList().getOrNull(id) ?: throw RegistryException(id, getMap().size)
    }

    override fun getMap(): Map<String, TrimMaterial> {
        return trimMaterials
    }
}

data class TrimMaterial(
    val identifier: String,
    val assetName: String,
    val color: String,
    val translate: String,
    val ingredient: String,
    val itemModelIndex: Float,
    val overrideArmorMaterials: Map<String, String>? = null,
): RegistryEntry() {

    override fun getIdentifier(): String {
        return identifier
    }
    override fun getProtocolId(): Int {
        return TrimMaterialRegistry.protocolIds.getOrThrow(identifier)
    }

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