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
        addEntry(TrimMaterial("minecraft:amethyst", "amethyst", "#9A5CC6", "trim_material.minecraft.amethyst", "minecraft:amethyst_shard"))
        addEntry(TrimMaterial("minecraft:copper", "copper", "#B4684D", "trim_material.minecraft.copper", "minecraft:copper_ingot"))
        addEntry(TrimMaterial("minecraft:diamond", "diamond", "#6EECD2", "trim_material.minecraft.diamond", "minecraft:diamond", mapOf("minecraft:diamond" to "diamond_darker")))
        addEntry(TrimMaterial("minecraft:emerald", "emerald", "#11A036", "trim_material.minecraft.emerald", "minecraft:emerald"))
        addEntry(TrimMaterial("minecraft:gold", "gold", "#DEB12D", "trim_material.minecraft.gold", "minecraft:gold_ingot", mapOf("minecraft:gold" to "gold_darker")))
        addEntry(TrimMaterial("minecraft:iron", "iron", "#ECECEC", "trim_material.minecraft.iron", "minecraft:iron_ingot",  mapOf("minecraft:iron" to "iron_darker")))
        addEntry(TrimMaterial("minecraft:lapis", "lapis", "#416E97", "trim_material.minecraft.lapis", "minecraft:lapis_lazuli", ))
        addEntry(TrimMaterial("minecraft:netherite", "netherite", "#625859", "trim_material.minecraft.netherite", "minecraft:netherite_ingot", mapOf("minecraft:netherite" to "netherite_darker")))
        addEntry(TrimMaterial("minecraft:quartz", "quartz", "#E3D4C4", "trim_material.minecraft.quartz", "minecraft:quartz"))
        addEntry(TrimMaterial("minecraft:redstone", "redstone", "#971607", "trim_material.minecraft.redstone", "minecraft:redstone"))
        updateCache()
    }

    override fun getCachedPacket(): ClientboundRegistryDataPacket {
        if(!::cachedPacket.isInitialized) updateCache()
        return cachedPacket
    }

    override fun updateCache() {
        cachedPacket = ClientboundRegistryDataPacket(this)
    }

    override fun get(identifier: String): TrimMaterial {
        return trimMaterials[identifier] ?: throw RegistryException(identifier, this.getMap().size)
    }

    override fun getOrNull(identifier: String): TrimMaterial? {
        return trimMaterials[identifier]
    }

    override fun getByProtocolId(id: Int): TrimMaterial {
        return trimMaterials.values.toList().getOrNull(id) ?: throw RegistryException(id, this.getMap().size)
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
    val overrideArmorMaterials: Map<String, String>? = null,
): RegistryEntry {

    override fun getProtocolId(): Int {
        return TrimMaterialRegistry.protocolIds.getOrThrow(identifier)
    }

    override fun getEntryIdentifier(): String {
        return identifier
    }


    override fun getNbt(): NBTCompound {
        return NBT.Compound {
            it.put("asset_name", assetName)
            it.put("description", NBT.Compound { desc ->
                desc.put("color", color)
                desc.put("translate", translate)
            })
            it.put("ingredient", ingredient)
        }
    }
}