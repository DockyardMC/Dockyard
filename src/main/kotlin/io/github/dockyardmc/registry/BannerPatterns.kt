package io.github.dockyardmc.registry

import io.github.dockyardmc.scroll.extensions.put
import io.github.dockyardmc.utils.Resources
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import org.jglrxavpok.hephaistos.parser.SNBTParser
import java.io.StringReader

object BannerPatterns {

    val map by lazy {
        val vanillaEntry = Resources.getFile("vanillaregistry/banner_pattern.snbt").split("\n")
        val list = mutableListOf<BannerPattern>()
        vanillaEntry.forEach { pattern ->
            val split = pattern.split(";")
            val identifier = split[0]
            val sNBT = split[1]

            val nbt = (SNBTParser(StringReader(sNBT))).parse() as NBTCompound
            list.add(BannerPattern.read(identifier, nbt))
        }
        list.associateBy { it.identifier }
    }

    fun getBannerPattern(identifier: String): BannerPattern =
        map[identifier] ?: error("Banner Pattern with identifier $identifier not found")

    val BASE = getBannerPattern("minecraft:base")
    val SQUARE_BOTTOM_LEFT = getBannerPattern("minecraft:square_bottom_left")
    val SQUARE_BOTTOM_RIGHT = getBannerPattern("minecraft:square_bottom_right")
    val SQUARE_TOP_LEFT = getBannerPattern("minecraft:square_top_left")
    val SQUARE_TOP_RIGHT = getBannerPattern("minecraft:square_top_right")
    val STRIPE_BOTTOM = getBannerPattern("minecraft:stripe_bottom")
    val STRIPE_TOP = getBannerPattern("minecraft:stripe_top")
    val STRIPE_LEFT = getBannerPattern("minecraft:stripe_left")
    val STRIPE_RIGHT = getBannerPattern("minecraft:stripe_right")
    val STRIPE_CENTER = getBannerPattern("minecraft:stripe_center")
    val STRIPE_MIDDLE = getBannerPattern("minecraft:stripe_middle")
    val STRIPE_DOWNRIGHT = getBannerPattern("minecraft:stripe_downright")
    val STRIPE_DOWNLEFT = getBannerPattern("minecraft:stripe_downleft")
    val SMALL_STRIPES = getBannerPattern("minecraft:small_stripes")
    val CROSS = getBannerPattern("minecraft:cross")
    val STRAIGHT_CROSS = getBannerPattern("minecraft:straight_cross")
    val TRIANGLE_BOTTOM = getBannerPattern("minecraft:triangle_bottom")
    val TRIANGLE_TOP = getBannerPattern("minecraft:triangle_top")
    val TRIANGLES_BOTTOM = getBannerPattern("minecraft:triangles_bottom")
    val TRIANGLES_TOP = getBannerPattern("minecraft:triangles_top")
    val DIAGONAL_LEFT = getBannerPattern("minecraft:diagonal_left")
    val DIAGONAL_UP_RIGHT = getBannerPattern("minecraft:diagonal_up_right")
    val DIAGONAL_UP_LEFT = getBannerPattern("minecraft:diagonal_up_left")
    val DIAGONAL_RIGHT = getBannerPattern("minecraft:diagonal_right")
    val CIRCLE = getBannerPattern("minecraft:circle")
    val RHOMBUS = getBannerPattern("minecraft:rhombus")
    val HALF_VERTICAL = getBannerPattern("minecraft:half_vertical")
    val HALF_HORIZONTAL = getBannerPattern("minecraft:half_horizontal")
    val HALF_VERTICAL_RIGHT = getBannerPattern("minecraft:half_vertical_right")
    val HALF_HORIZONTAL_BOTTOM = getBannerPattern("minecraft:half_horizontal_bottom")
    val BORDER = getBannerPattern("minecraft:border")
    val CURLY_BORDER = getBannerPattern("minecraft:curly_border")
    val GRADIENT = getBannerPattern("minecraft:gradient")
    val GRADIENT_UP = getBannerPattern("minecraft:gradient_up")
    val BRICKS = getBannerPattern("minecraft:bricks")
    val GLOBE = getBannerPattern("minecraft:globe")
    val CREEPER = getBannerPattern("minecraft:creeper")
    val SKULL = getBannerPattern("minecraft:skull")
    val FLOWER = getBannerPattern("minecraft:flower")
    val MOJANG = getBannerPattern("minecraft:mojang")
    val PIGLIN = getBannerPattern("minecraft:piglin")
    val FLOW = getBannerPattern("minecraft:flow")
    val GUSTER = getBannerPattern("minecraft:guster")

    lateinit var registryCache: Registry

    fun cacheRegistry() {
        val registryEntries = mutableListOf<RegistryEntry>()
        map.forEach {
            registryEntries.add(RegistryEntry(it.key, it.value.toNBT()))
        }

        registryCache =  Registry("minecraft:banner_pattern", registryEntries)
    }

    init {
        cacheRegistry()
    }
}

data class BannerPattern(
    val identifier: String,
    val assetId: String,
    val translationKey: String,
) {
    val id: Int get() = BannerPatterns.map.values.indexOf(this)

    fun toNBT(): NBTCompound {
        return NBT.Compound {
            it.put("asset_id", this.assetId)
            it.put("translation_key", this.assetId)
        }
    }

    companion object {
        fun read(identifier: String, nbt: NBTCompound): BannerPattern {
            return BannerPattern(
                identifier,
                nbt.getString("asset_id")!!,
                nbt.getString("translation_key")!!
            )
        }
    }
}