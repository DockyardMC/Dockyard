package io.github.dockyardmc.inventory

import io.github.dockyardmc.registry.Block
import io.github.dockyardmc.registry.Blocks

class ItemStack(var material: Block, var amount: Int) {

    companion object {
        val air = ItemStack(Blocks.AIR, 1)
    }
}