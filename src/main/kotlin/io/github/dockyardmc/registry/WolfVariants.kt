package io.github.dockyardmc.registry

import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound

object WolfVariants {

    val map: Map<String, WolfVariant> = mapOf(
        "minecraft:ashen" to WolfVariant(
            wildTexture = "minecraft:entity/wolf/wolf_ashen",
            angryTexture = "minecraft:entity/wolf/wolf_ashen_angry",
            biomes = "minecraft:snowy_taiga",
            tameTexture = "minecraft:entity/wolf/wolf_ashen_tame"
        ),
        "minecraft:black" to WolfVariant(
            wildTexture = "minecraft:entity/wolf/wolf_black",
            angryTexture = "minecraft:entity/wolf/wolf_black_angry",
            biomes = "minecraft:old_growth_pine_taiga",
            tameTexture = "minecraft:entity/wolf/wolf_black_tame"
        ),
        "minecraft:chestnut" to WolfVariant(
            wildTexture = "minecraft:entity/wolf/wolf_chestnut",
            angryTexture = "minecraft:entity/wolf/wolf_chestnut_angry",
            biomes = "minecraft:old_growth_spruce_taiga",
            tameTexture = "minecraft:entity/wolf/wolf_chestnut_tame"
        ),
        "minecraft:pale" to WolfVariant(
            wildTexture = "minecraft:entity/wolf/wolf",
            angryTexture = "minecraft:entity/wolf/wolf_angry",
            biomes = "minecraft:taiga",
            tameTexture = "minecraft:entity/wolf/wolf_tame"
        ),
        "minecraft:rusty" to WolfVariant(
            wildTexture = "minecraft:entity/wolf/wolf_rusty",
            angryTexture = "minecraft:entity/wolf/wolf_rusty_angry",
            biomes = "#minecraft:is_jungle",
            tameTexture = "minecraft:entity/wolf/wolf_rusty_tame"
        ),
        "minecraft:snowy" to WolfVariant(
            wildTexture = "minecraft:entity/wolf/wolf_snowy",
            angryTexture = "minecraft:entity/wolf/wolf_snowy_angry",
            biomes = "minecraft:grove",
            tameTexture = "minecraft:entity/wolf/wolf_snowy_tame"
        ),
        "minecraft:spotted" to WolfVariant(
            wildTexture = "minecraft:entity/wolf/wolf_spotted",
            angryTexture = "minecraft:entity/wolf/wolf_spotted_angry",
            biomes = "#minecraft:is_savanna",
            tameTexture = "minecraft:entity/wolf/wolf_spotted_tame"
        ),
        "minecraft:striped" to WolfVariant(
            wildTexture = "minecraft:entity/wolf/wolf_striped",
            angryTexture = "minecraft:entity/wolf/wolf_striped_angry",
            biomes = "#minecraft:is_badlands",
            tameTexture = "minecraft:entity/wolf/wolf_striped_tame"
        ),
        "minecraft:woods" to WolfVariant(
            wildTexture = "minecraft:entity/wolf/wolf_woods",
            angryTexture = "minecraft:entity/wolf/wolf_woods_angry",
            biomes = "minecraft:forest",
            tameTexture = "minecraft:entity/wolf/wolf_woods_tame"
        )
    )

    lateinit var registryCache: Registry

    fun cacheRegistry() {
        val entries = mutableListOf<RegistryEntry>()
        map.forEach { entries.add(RegistryEntry(it.key, it.value.toNBT())) }
        registryCache = Registry("minecraft:wolf_variant", entries)
    }

    init {
        cacheRegistry()
    }

}

data class WolfVariant(
    val wildTexture: String,
    val angryTexture: String,
    val biomes: String,
    val tameTexture: String
) {

    fun toNBT(): NBTCompound {
        return NBT.Compound {
            it.put("wild_texture", wildTexture)
            it.put("angry_texture", angryTexture)
            it.put("biomes", biomes)
            it.put("tame_texture", tameTexture)
        }
    }

}