package io.github.dockyardmc.inventory

import io.github.dockyardmc.registry.Block
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.Item
import io.github.dockyardmc.registry.Items

class ItemStack(var material: Item, var amount: Int) {

    companion object {
        val air = ItemStack(Items.AIR, 1)
    }
}