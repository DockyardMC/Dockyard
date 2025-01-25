package io.github.dockyardmc.events

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.inventory.EntityInventory
import io.github.dockyardmc.item.ItemStack

class InventoryItemChangeEvent(
    val entity: Entity,
    val inventory: EntityInventory,
    var slot: Int,
    var newItem: ItemStack,
    var oldItem: ItemStack,
    override val context: Event.Context
): Event {
}