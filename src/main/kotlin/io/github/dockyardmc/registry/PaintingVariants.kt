package io.github.dockyardmc.registry

import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound

object PaintingVariants {

    val map = mutableMapOf<String, PaintingVariant>(
        "minecraft:kebab" to PaintingVariant("kebab", 1, 1),
        "minecraft:aztec" to PaintingVariant("aztec", 1, 1),
        "minecraft:alban" to PaintingVariant("alban", 1, 1),
        "minecraft:aztec2" to PaintingVariant("aztec2", 1, 1),
        "minecraft:bomb" to PaintingVariant("bomb", 1, 1),
        "minecraft:plant" to PaintingVariant("plant", 1, 1),
        "minecraft:wasteland" to PaintingVariant("wasteland", 1, 1),
        "minecraft:pool" to PaintingVariant("pool", 1, 2),
        "minecraft:courbet" to PaintingVariant("courbet", 1, 2),
        "minecraft:sea" to PaintingVariant("sea", 1, 2),
        "minecraft:sunset" to PaintingVariant("sunset", 1, 2),
        "minecraft:creebet" to PaintingVariant("creebet", 1, 2),
        "minecraft:wanderer" to PaintingVariant("wanderer", 2, 1),
        "minecraft:graham" to PaintingVariant("graham", 2, 1),
        "minecraft:match" to PaintingVariant("match", 2, 2),
        "minecraft:bust" to PaintingVariant("bust", 2, 2),
        "minecraft:stage" to PaintingVariant("stage", 2, 2),
        "minecraft:void" to PaintingVariant("void", 2, 2),
        "minecraft:skull_and_roses" to PaintingVariant("skull_and_roses", 2, 2),
        "minecraft:wither" to PaintingVariant("wither", 2, 2),
        "minecraft:fighters" to PaintingVariant("fighters", 2, 4),
        "minecraft:pointer" to PaintingVariant("pointer", 4, 4),
        "minecraft:pigscene" to PaintingVariant("pigscene", 4, 4),
        "minecraft:burning_skull" to PaintingVariant("burning_skull", 4, 4),
        "minecraft:skeleton" to PaintingVariant("skeleton", 3, 4),
        "minecraft:earth" to PaintingVariant("earth", 2, 2),
        "minecraft:wind" to PaintingVariant("wind", 2, 2),
        "minecraft:water" to PaintingVariant("water", 2, 2),
        "minecraft:fire" to PaintingVariant("fire", 2, 2),
        "minecraft:donkey_kong" to PaintingVariant("donkey_kong", 3, 4),
    )

    val KEBAB = map["minecraft:kebab"]!!
    val AZTEC = map["minecraft:aztec"]!!
    val ALBAN = map["minecraft:alban"]!!
    val AZTEC2 = map["minecraft:aztec2"]!!
    val BOMB = map["minecraft:bomb"]!!
    val PLANT = map["minecraft:plant"]!!
    val WASTELAND = map["minecraft:wasteland"]!!
    val POOL = map["minecraft:pool"]!!
    val COURBET = map["minecraft:courbet"]!!
    val SEA = map["minecraft:sea"]!!
    val SUNSET = map["minecraft:sunset"]!!
    val CREEBET = map["minecraft:creebet"]!!
    val WANDERER = map["minecraft:wanderer"]!!
    val GRAHAM = map["minecraft:graham"]!!
    val MATCH = map["minecraft:match"]!!
    val BUST = map["minecraft:bust"]!!
    val STAGE = map["minecraft:stage"]!!
    val VOID = map["minecraft:void"]!!
    val SKULL_AND_ROSES = map["minecraft:skull_and_roses"]!!
    val WITHER = map["minecraft:wither"]!!
    val FIGHTERS = map["minecraft:fighters"]!!
    val POINTER = map["minecraft:pointer"]!!
    val PIGSCENE = map["minecraft:pigscene"]!!
    val BURNING_SKULL = map["minecraft:burning_skull"]!!
    val SKELETON = map["minecraft:skeleton"]!!
    val EARTH = map["minecraft:earth"]!!
    val WIND = map["minecraft:wind"]!!
    val WATER = map["minecraft:water"]!!
    val FIRE = map["minecraft:fire"]!!
    val DONKEY_KONG = map["minecraft:donkey_kong"]!!

    lateinit var registryCache: Registry

    fun cacheRegistry() {
        val entries = mutableListOf<RegistryEntry>()
        map.forEach { entries.add(RegistryEntry(it.key, it.value.toNBT())) }
        registryCache = Registry("minecraft:painting_variant", entries)
    }

    init {
        cacheRegistry()
    }
}

data class PaintingVariant(
    val assetId: String,
    val height: Int,
    val width: Int
) {
    fun toNBT(): NBTCompound {
        return NBT.Compound {
            it.put("asset_id", assetId)
            it.put("height", height)
            it.put("width", width)
        }
    }
}