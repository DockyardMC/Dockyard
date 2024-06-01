package io.github.dockyardmc.material

object Materials {
    val AIR = Material(0, "Air", "minecraft:stone", MaterialType.BLOCK)
    val STONE = Material(1, "Stone", "minecraft:stone", MaterialType.BOTH)
}


data class Material(var blockStateId: Int, var name: String, var namespace: String, var type: MaterialType)

enum class MaterialType {
    ITEM,
    BLOCK,
    BOTH
}