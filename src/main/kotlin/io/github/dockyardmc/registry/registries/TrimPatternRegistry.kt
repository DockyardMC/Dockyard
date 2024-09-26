package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.protocol.packets.configurations.ClientboundRegistryDataPacket
import io.github.dockyardmc.registry.DynamicRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import java.util.concurrent.atomic.AtomicInteger

object TrimPatternRegistry: DynamicRegistry {

    override val identifier: String = "minecraft:trim_pattern"

    private lateinit var cachedPacket: ClientboundRegistryDataPacket

    val trimPatterns: MutableMap<String, TrimPattern> = mutableMapOf()
    val protocolIdCounter =  AtomicInteger()

    init {
        trimPatterns["minecraft:bolt"] = TrimPattern("minecraft:bolt", false, "trim_pattern.minecraft.bolt" ,"minecraft:bolt_armor_trim_smithing_template", protocolIdCounter.getAndIncrement())
        trimPatterns["minecraft:coast"] = TrimPattern("minecraft:coast", false, "trim_pattern.minecraft.coast" ,"minecraft:coast_armor_trim_smithing_template", protocolIdCounter.getAndIncrement())
        trimPatterns["minecraft:dune"] = TrimPattern("minecraft:dune", false, "trim_pattern.minecraft.dune" ,"minecraft:dune_armor_trim_smithing_template", protocolIdCounter.getAndIncrement())
        trimPatterns["minecraft:eye"] = TrimPattern("minecraft:eye", false, "trim_pattern.minecraft.eye" ,"minecraft:eye_armor_trim_smithing_template", protocolIdCounter.getAndIncrement())
        trimPatterns["minecraft:flow"] = TrimPattern("minecraft:flow", false, "trim_pattern.minecraft.flow" ,"minecraft:flow_armor_trim_smithing_template", protocolIdCounter.getAndIncrement())
        trimPatterns["minecraft:host"] = TrimPattern("minecraft:host", false, "trim_pattern.minecraft.host" ,"minecraft:host_armor_trim_smithing_template", protocolIdCounter.getAndIncrement())
        trimPatterns["minecraft:raiser"] = TrimPattern("minecraft:raiser", false, "trim_pattern.minecraft.raiser" ,"minecraft:raiser_armor_trim_smithing_template", protocolIdCounter.getAndIncrement())
        trimPatterns["minecraft:rib"] = TrimPattern("minecraft:rib", false, "trim_pattern.minecraft.rib" ,"minecraft:rib_armor_trim_smithing_template", protocolIdCounter.getAndIncrement())
        trimPatterns["minecraft:sentry"] = TrimPattern("minecraft:sentry", false, "trim_pattern.minecraft.sentry" ,"minecraft:sentry_armor_trim_smithing_template", protocolIdCounter.getAndIncrement())
        trimPatterns["minecraft:shaper"] = TrimPattern("minecraft:shaper", false, "trim_pattern.minecraft.shaper" ,"minecraft:shaper_armor_trim_smithing_template", protocolIdCounter.getAndIncrement())
        trimPatterns["minecraft:silence"] = TrimPattern("minecraft:silence", false, "trim_pattern.minecraft.silence" ,"minecraft:silence_armor_trim_smithing_template", protocolIdCounter.getAndIncrement())
        trimPatterns["minecraft:snout"] = TrimPattern("minecraft:snout", false, "trim_pattern.minecraft.snout" ,"minecraft:snout_armor_trim_smithing_template", protocolIdCounter.getAndIncrement())
        trimPatterns["minecraft:spire"] = TrimPattern("minecraft:spire", false, "trim_pattern.minecraft.spire" ,"minecraft:spire_armor_trim_smithing_template", protocolIdCounter.getAndIncrement())
        trimPatterns["minecraft:tide"] = TrimPattern("minecraft:tide", false, "trim_pattern.minecraft.tide" ,"minecraft:tide_armor_trim_smithing_template", protocolIdCounter.getAndIncrement())
        trimPatterns["minecraft:vex"] = TrimPattern("minecraft:vex", false, "trim_pattern.minecraft.vex" ,"minecraft:vex_armor_trim_smithing_template", protocolIdCounter.getAndIncrement())
        trimPatterns["minecraft:ward"] = TrimPattern("minecraft:ward", false, "trim_pattern.minecraft.ward" ,"minecraft:ward_armor_trim_smithing_template", protocolIdCounter.getAndIncrement())
        trimPatterns["minecraft:wayfinder"] = TrimPattern("minecraft:wayfinder", false, "trim_pattern.minecraft.wayfinder" ,"minecraft:wayfinder_armor_trim_smithing_template", protocolIdCounter.getAndIncrement())
        trimPatterns["minecraft:wild"] = TrimPattern("minecraft:wild", false, "trim_pattern.minecraft.wild" ,"minecraft:wild_armor_trim_smithing_template", protocolIdCounter.getAndIncrement())
    }

    override fun getCachedPacket(): ClientboundRegistryDataPacket {
        if(!::cachedPacket.isInitialized) updateCache()
        return cachedPacket
    }

    override fun updateCache() {
        cachedPacket = ClientboundRegistryDataPacket(this)
    }

    override fun get(identifier: String): TrimPattern {
        return trimPatterns[identifier] ?: throw IllegalStateException("There is no registry entry with identifier $identifier")
    }

    override fun getOrNull(identifier: String): TrimPattern? {
        return trimPatterns[identifier]
    }

    override fun getByProtocolId(id: Int): TrimPattern {
        return trimPatterns.values.toList().getOrNull(id) ?: throw IllegalStateException("There is no registry entry with protocol id $id")
    }

    override fun getMap(): Map<String, TrimPattern> {
        return trimPatterns
    }
}

data class TrimPattern(
    val assetId: String,
    val decal: Boolean,
    val translate: String,
    val templateItem: String,
    override val protocolId: Int
): RegistryEntry {

    override fun getNbt(): NBTCompound {
        return NBT.Compound {
            it.put("asset_id", assetId)
            it.put("decal", decal)
            it.put("description", NBT.Compound { desc ->
                desc.put("translate", translate)
            })
            it.put("template_item", templateItem)
        }
    }
}