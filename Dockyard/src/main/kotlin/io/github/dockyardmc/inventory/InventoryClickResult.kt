package io.github.dockyardmc.inventory

import io.github.dockyardmc.item.ItemStack

data class InventoryClickResult(
    var clicked: ItemStack,
    var cursor: ItemStack,
    var cancelled: Boolean
) {
    fun cancelled(): InventoryClickResult {
        cancelled = true
        return this
    }
}