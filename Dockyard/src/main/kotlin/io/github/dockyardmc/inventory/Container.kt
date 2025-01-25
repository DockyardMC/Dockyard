package io.github.dockyardmc.inventory

import io.github.dockyardmc.item.ItemStack

interface ContainerInventory {
    var name: String
    var rows: Int
    var contents: MutableMap<Int, ItemStack>
}