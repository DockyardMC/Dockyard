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

object BannerPatternRegistry: DynamicRegistry {

    override val identifier: String = "minecraft:banner_pattern"

    private lateinit var cachedPacket: ClientboundRegistryDataPacket

    val bannerPatterns: MutableMap<String, BannerPattern> = mutableMapOf()
    val protocolIds: MutableMap<String, Int> = mutableMapOf()
    private val protocolIdCounter =  AtomicInteger()

    override fun getMaxProtocolId(): Int {
        return protocolIdCounter.get()
    }

    fun addEntry(entry: BannerPattern, updateCache: Boolean = true) {
        protocolIds[entry.identifier] = protocolIdCounter.getAndIncrement()
        bannerPatterns[entry.identifier] = entry
        if(updateCache) updateCache()
    }

    override fun register() {
        addEntry(BannerPattern("minecraft:base", "block.minecraft.banner.base"), false)
        addEntry(BannerPattern("minecraft:border", "block.minecraft.banner.border"), false)
        addEntry(BannerPattern("minecraft:bricks", "block.minecraft.banner.bricks"), false)
        addEntry(BannerPattern("minecraft:circle", "block.minecraft.banner.circle"), false)
        addEntry(BannerPattern("minecraft:creeper", "block.minecraft.banner.creeper"), false)
        addEntry(BannerPattern("minecraft:cross", "block.minecraft.banner.cross"), false)
        addEntry(BannerPattern("minecraft:curly_border", "block.minecraft.banner.curly_border"), false)
        addEntry(BannerPattern("minecraft:diagonal_left", "block.minecraft.banner.diagonal_left"), false)
        addEntry(BannerPattern("minecraft:diagonal_right", "block.minecraft.banner.diagonal_right"), false)
        addEntry(BannerPattern("minecraft:diagonal_up_left", "block.minecraft.banner.diagonal_up_left"), false)
        addEntry(BannerPattern("minecraft:diagonal_up_right", "block.minecraft.banner.diagonal_up_right"), false)
        addEntry(BannerPattern("minecraft:flow", "block.minecraft.banner.flow"), false)
        addEntry(BannerPattern("minecraft:flower", "block.minecraft.banner.flower"), false)
        addEntry(BannerPattern("minecraft:globe", "block.minecraft.banner.globe"), false)
        addEntry(BannerPattern("minecraft:gradient", "block.minecraft.banner.gradient"), false)
        addEntry(BannerPattern("minecraft:gradient_up", "block.minecraft.banner.gradient_up"), false)
        addEntry(BannerPattern("minecraft:guster", "block.minecraft.banner.guster"), false)
        addEntry(BannerPattern("minecraft:half_horizontal", "block.minecraft.banner.half_horizontal"), false)
        addEntry(BannerPattern("minecraft:half_horizontal_bottom", "block.minecraft.banner.half_horizontal_bottom"), false)
        addEntry(BannerPattern("minecraft:half_vertical", "block.minecraft.banner.half_vertical"), false)
        addEntry(BannerPattern("minecraft:half_vertical_right", "block.minecraft.banner.half_vertical_right"), false)
        addEntry(BannerPattern("minecraft:mojang", "block.minecraft.banner.mojang"), false)
        addEntry(BannerPattern("minecraft:piglin", "block.minecraft.banner.piglin"), false)
        addEntry(BannerPattern("minecraft:rhombus", "block.minecraft.banner.rhombus"), false)
        addEntry(BannerPattern("minecraft:skull", "block.minecraft.banner.skull"), false)
        addEntry(BannerPattern("minecraft:small_stripes", "block.minecraft.banner.small_stripes"), false)
        addEntry(BannerPattern("minecraft:square_bottom_left", "block.minecraft.banner.square_bottom_left"), false)
        addEntry(BannerPattern("minecraft:square_bottom_right", "block.minecraft.banner.square_bottom_right"), false)
        addEntry(BannerPattern("minecraft:square_top_left", "block.minecraft.banner.square_top_left"), false)
        addEntry(BannerPattern("minecraft:square_top_right", "block.minecraft.banner.square_top_right"), false)
        addEntry(BannerPattern("minecraft:straight_cross", "block.minecraft.banner.straight_cross"), false)
        addEntry(BannerPattern("minecraft:stripe_bottom", "block.minecraft.banner.stripe_bottom"), false)
        addEntry(BannerPattern("minecraft:stripe_center", "block.minecraft.banner.stripe_center"), false)
        addEntry(BannerPattern("minecraft:stripe_downleft", "block.minecraft.banner.stripe_downleft"), false)
        addEntry(BannerPattern("minecraft:stripe_downright", "block.minecraft.banner.stripe_downright"), false)
        addEntry(BannerPattern("minecraft:stripe_left", "block.minecraft.banner.stripe_left"), false)
        addEntry(BannerPattern("minecraft:stripe_middle", "block.minecraft.banner.stripe_middle"), false)
        addEntry(BannerPattern("minecraft:stripe_right", "block.minecraft.banner.stripe_right"), false)
        addEntry(BannerPattern("minecraft:stripe_top", "block.minecraft.banner.stripe_top"), false)
        addEntry(BannerPattern("minecraft:triangle_bottom", "block.minecraft.banner.triangle_bottom"), false)
        addEntry(BannerPattern("minecraft:triangle_top", "block.minecraft.banner.triangle_top"), false)
        addEntry(BannerPattern("minecraft:triangles_bottom", "block.minecraft.banner.triangles_bottom"), false)
        addEntry(BannerPattern("minecraft:triangles_top", "block.minecraft.banner.triangles_top"), false)
        updateCache()
    }

    override fun getCachedPacket(): ClientboundRegistryDataPacket {
        if(!::cachedPacket.isInitialized) updateCache()
        return cachedPacket
    }

    override fun updateCache() {
        cachedPacket = ClientboundRegistryDataPacket(this)
    }


    override fun get(identifier: String): BannerPattern {
        return bannerPatterns[identifier] ?: throw throw RegistryException(identifier, this.getMap().size)
    }

    override fun getOrNull(identifier: String): BannerPattern? {
        return bannerPatterns[identifier]
    }

    override fun getByProtocolId(id: Int): BannerPattern {
        return bannerPatterns.values.toList().getOrNull(id) ?: throw throw RegistryException(identifier, this.getMap().size)
    }

    override fun getMap(): Map<String, BannerPattern> {
        return bannerPatterns
    }
}

data class BannerPattern(
    val identifier: String,
    val translationKey: String,
): RegistryEntry {

    override fun getProtocolId(): Int {
        return BannerPatternRegistry.protocolIds.getOrThrow(identifier)
    }

    override fun getNbt(): NBTCompound {
        return NBT.Compound {
            it.put("asset_id", this.identifier)
            it.put("translation_key", this.translationKey)
        }
    }
}
