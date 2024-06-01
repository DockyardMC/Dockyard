package io.github.dockyardmc.inventory

import io.github.dockyardmc.material.Material
import io.github.dockyardmc.material.Materials

class ItemStack(var material: Material, var amount: Int) {

    companion object {
        val air = ItemStack(Materials.AIR, 1)
    }

}