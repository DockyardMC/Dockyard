package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.inventory.EntityInventory
import io.github.dockyardmc.item.ItemStack

@EventDocumentation("when a slot is set in entity's inventory")
data class InventoryItemChangeEvent(
    val entity: Entity,
    val inventory: EntityInventory,
    var slot: Int,
    var newItem: ItemStack,
    var oldItem: ItemStack,
    override val context: Event.Context
) : Event