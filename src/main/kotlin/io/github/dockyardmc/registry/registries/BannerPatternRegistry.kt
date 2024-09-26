package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.protocol.packets.configurations.ClientboundRegistryDataPacket
import io.github.dockyardmc.registry.DynamicRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import java.util.concurrent.atomic.AtomicInteger

object BannerPatternRegistry: DynamicRegistry {

    override val identifier: String = "minecraft:banner_pattern"

    private lateinit var cachedPacket: ClientboundRegistryDataPacket

    val bannerPatterns: MutableMap<String, BannerPattern> = mutableMapOf()
    val protocolIdCounter =  AtomicInteger()

    init {
        bannerPatterns["minecraft:base"] = BannerPattern("minecraft:base", "block.minecraft.banner.base", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:border"] = BannerPattern("minecraft:border", "block.minecraft.banner.border", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:bricks"] = BannerPattern("minecraft:bricks", "block.minecraft.banner.bricks", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:circle"] = BannerPattern("minecraft:circle", "block.minecraft.banner.circle", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:creeper"] = BannerPattern("minecraft:creeper", "block.minecraft.banner.creeper", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:cross"] = BannerPattern("minecraft:cross", "block.minecraft.banner.cross", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:curly_border"] = BannerPattern("minecraft:curly_border", "block.minecraft.banner.curly_border", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:diagonal_left"] = BannerPattern("minecraft:diagonal_left", "block.minecraft.banner.diagonal_left", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:diagonal_right"] = BannerPattern("minecraft:diagonal_right", "block.minecraft.banner.diagonal_right", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:diagonal_up_left"] = BannerPattern("minecraft:diagonal_up_left", "block.minecraft.banner.diagonal_up_left", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:diagonal_up_right"] = BannerPattern("minecraft:diagonal_up_right", "block.minecraft.banner.diagonal_up_right", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:flow"] = BannerPattern("minecraft:flow", "block.minecraft.banner.flow", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:flower"] = BannerPattern("minecraft:flower", "block.minecraft.banner.flower", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:globe"] = BannerPattern("minecraft:globe", "block.minecraft.banner.globe", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:gradient"] = BannerPattern("minecraft:gradient", "block.minecraft.banner.gradient", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:gradient_up"] = BannerPattern("minecraft:gradient_up", "block.minecraft.banner.gradient_up", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:guster"] = BannerPattern("minecraft:guster", "block.minecraft.banner.guster", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:half_horizontal"] = BannerPattern("minecraft:half_horizontal", "block.minecraft.banner.half_horizontal", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:half_horizontal_bottom"] = BannerPattern("minecraft:half_horizontal_bottom", "block.minecraft.banner.half_horizontal_bottom", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:half_vertical"] = BannerPattern("minecraft:half_vertical", "block.minecraft.banner.half_vertical", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:half_vertical_right"] = BannerPattern("minecraft:half_vertical_right", "block.minecraft.banner.half_vertical_right", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:mojang"] = BannerPattern("minecraft:mojang", "block.minecraft.banner.mojang", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:piglin"] = BannerPattern("minecraft:piglin", "block.minecraft.banner.piglin", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:rhombus"] = BannerPattern("minecraft:rhombus", "block.minecraft.banner.rhombus", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:skull"] = BannerPattern("minecraft:skull", "block.minecraft.banner.skull", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:small_stripes"] = BannerPattern("minecraft:small_stripes", "block.minecraft.banner.small_stripes", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:square_bottom_left"] = BannerPattern("minecraft:square_bottom_left", "block.minecraft.banner.square_bottom_left", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:square_bottom_right"] = BannerPattern("minecraft:square_bottom_right", "block.minecraft.banner.square_bottom_right", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:square_top_left"] = BannerPattern("minecraft:square_top_left", "block.minecraft.banner.square_top_left", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:square_top_right"] = BannerPattern("minecraft:square_top_right", "block.minecraft.banner.square_top_right", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:straight_cross"] = BannerPattern("minecraft:straight_cross", "block.minecraft.banner.straight_cross", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:stripe_bottom"] = BannerPattern("minecraft:stripe_bottom", "block.minecraft.banner.stripe_bottom", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:stripe_center"] = BannerPattern("minecraft:stripe_center", "block.minecraft.banner.stripe_center", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:stripe_downleft"] = BannerPattern("minecraft:stripe_downleft", "block.minecraft.banner.stripe_downleft", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:stripe_downright"] = BannerPattern("minecraft:stripe_downright", "block.minecraft.banner.stripe_downright", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:stripe_left"] = BannerPattern("minecraft:stripe_left", "block.minecraft.banner.stripe_left", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:stripe_middle"] = BannerPattern("minecraft:stripe_middle", "block.minecraft.banner.stripe_middle", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:stripe_right"] = BannerPattern("minecraft:stripe_right", "block.minecraft.banner.stripe_right", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:stripe_top"] = BannerPattern("minecraft:stripe_top", "block.minecraft.banner.stripe_top", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:triangle_bottom"] = BannerPattern("minecraft:triangle_bottom", "block.minecraft.banner.triangle_bottom", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:triangle_top"] = BannerPattern("minecraft:triangle_top", "block.minecraft.banner.triangle_top", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:triangles_bottom"] = BannerPattern("minecraft:triangles_bottom", "block.minecraft.banner.triangles_bottom", protocolIdCounter.getAndIncrement())
        bannerPatterns["minecraft:triangles_top"] = BannerPattern("minecraft:triangles_top", "block.minecraft.banner.triangles_top", protocolIdCounter.getAndIncrement())
    }

    override fun getCachedPacket(): ClientboundRegistryDataPacket {
        if(!::cachedPacket.isInitialized) updateCache()
        return cachedPacket
    }

    override fun updateCache() {
        cachedPacket = ClientboundRegistryDataPacket(this)
    }

    override fun get(identifier: String): BannerPattern {
        return bannerPatterns[identifier] ?: throw IllegalStateException("There is no registry entry with identifier $identifier")
    }

    override fun getOrNull(identifier: String): BannerPattern? {
        return bannerPatterns[identifier]
    }

    override fun getByProtocolId(id: Int): BannerPattern {
        return bannerPatterns.values.toList().getOrNull(id) ?: throw IllegalStateException("There is no registry entry with protocol id $id")
    }

    override fun getMap(): Map<String, BannerPattern> {
        return bannerPatterns
    }
}

data class BannerPattern(
    val assetId: String,
    val translationKey: String,
    override val protocolId: Int,
): RegistryEntry {

    override fun getNbt(): NBTCompound {
        return NBT.Compound {
            it.put("asset_id", this.assetId)
            it.put("translation_key", this.assetId)
        }
    }
}
