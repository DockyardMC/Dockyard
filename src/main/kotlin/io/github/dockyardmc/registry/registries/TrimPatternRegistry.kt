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

object TrimPatternRegistry: DynamicRegistry {

    override val identifier: String = "minecraft:trim_pattern"

    private lateinit var cachedPacket: ClientboundRegistryDataPacket

    override fun getMaxProtocolId(): Int {
        return protocolIdCounter.get()
    }

    val trimPatterns: MutableMap<String, TrimPattern> = mutableMapOf()
    val protocolIds: MutableMap<String, Int> = mutableMapOf()
    private val protocolIdCounter = AtomicInteger()

    fun addEntry(entry: TrimPattern, updateCache: Boolean = true) {
        protocolIds[entry.identifier] = protocolIdCounter.getAndIncrement()
        trimPatterns[entry.identifier] = entry
        if (updateCache) updateCache()
    }

    override fun register() {
        addEntry(TrimPattern("minecraft:bolt", "bolt", false, "trim_pattern.minecraft.bolt" ,"minecraft:bolt_armor_trim_smithing_template"))
        addEntry(TrimPattern("minecraft:coast", "coast", false, "trim_pattern.minecraft.coast" ,"minecraft:coast_armor_trim_smithing_template"))
        addEntry(TrimPattern("minecraft:dune", "dune", false, "trim_pattern.minecraft.dune" ,"minecraft:dune_armor_trim_smithing_template"))
        addEntry(TrimPattern("minecraft:eye", "eye", false, "trim_pattern.minecraft.eye" ,"minecraft:eye_armor_trim_smithing_template"))
        addEntry(TrimPattern("minecraft:flow", "flow", false, "trim_pattern.minecraft.flow" ,"minecraft:flow_armor_trim_smithing_template"))
        addEntry(TrimPattern("minecraft:host", "host", false, "trim_pattern.minecraft.host" ,"minecraft:host_armor_trim_smithing_template"))
        addEntry(TrimPattern("minecraft:raiser", "raiser", false, "trim_pattern.minecraft.raiser" ,"minecraft:raiser_armor_trim_smithing_template"))
        addEntry(TrimPattern("minecraft:rib", "rib", false, "trim_pattern.minecraft.rib" ,"minecraft:rib_armor_trim_smithing_template"))
        addEntry(TrimPattern("minecraft:sentry", "sentry", false, "trim_pattern.minecraft.sentry" ,"minecraft:sentry_armor_trim_smithing_template"))
        addEntry(TrimPattern("minecraft:shaper", "shaper", false, "trim_pattern.minecraft.shaper" ,"minecraft:shaper_armor_trim_smithing_template"))
        addEntry(TrimPattern("minecraft:silence", "silence", false, "trim_pattern.minecraft.silence" ,"minecraft:silence_armor_trim_smithing_template"))
        addEntry(TrimPattern("minecraft:snout", "snout", false, "trim_pattern.minecraft.snout" ,"minecraft:snout_armor_trim_smithing_template"))
        addEntry(TrimPattern("minecraft:spire", "spire", false, "trim_pattern.minecraft.spire" ,"minecraft:spire_armor_trim_smithing_template"))
        addEntry(TrimPattern("minecraft:tide", "tide", false, "trim_pattern.minecraft.tide" ,"minecraft:tide_armor_trim_smithing_template"))
        addEntry(TrimPattern("minecraft:vex", "vex", false, "trim_pattern.minecraft.vex" ,"minecraft:vex_armor_trim_smithing_template"))
        addEntry(TrimPattern("minecraft:ward", "ward", false, "trim_pattern.minecraft.ward" ,"minecraft:ward_armor_trim_smithing_template"))
        addEntry(TrimPattern("minecraft:wayfinder", "wayfinder", false, "trim_pattern.minecraft.wayfinder" ,"minecraft:wayfinder_armor_trim_smithing_template"))
        addEntry(TrimPattern("minecraft:wild", "wild", false, "trim_pattern.minecraft.wild" ,"minecraft:wild_armor_trim_smithing_template"))
        updateCache()
    }

    override fun getCachedPacket(): ClientboundRegistryDataPacket {
        if(!::cachedPacket.isInitialized) updateCache()
        return cachedPacket
    }

    override fun updateCache() {
        cachedPacket = ClientboundRegistryDataPacket(this)
    }

    override fun get(identifier: String): TrimPattern {
        return trimPatterns[identifier] ?: throw RegistryException(identifier, this.getMap().size)
    }

    override fun getOrNull(identifier: String): TrimPattern? {
        return trimPatterns[identifier]
    }

    override fun getByProtocolId(id: Int): TrimPattern {
        return trimPatterns.values.toList().getOrNull(id) ?: throw RegistryException(id, this.getMap().size)
    }

    override fun getMap(): Map<String, TrimPattern> {
        return trimPatterns
    }
}

data class TrimPattern(
    val identifier: String,
    val assetId: String,
    val decal: Boolean,
    val translate: String,
    val templateItem: String,
): RegistryEntry {

    override fun getProtocolId(): Int {
        return PaintingVariantRegistry.protocolIds.getOrThrow(identifier)
    }

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