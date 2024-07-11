package io.github.dockyardmc.inventory

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.Player

interface ContainerInventory {
    val name: String
    val rows: Int
    var innerContainerContents: MutableMap<Int, ItemStack>

    fun open(player: Player)
}