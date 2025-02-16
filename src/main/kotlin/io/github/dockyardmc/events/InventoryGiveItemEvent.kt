package io.github.dockyardmc.events

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.item.ItemStack

class InventoryGiveItemEvent(val entity: Entity, val item: ItemStack, val success: Boolean, override val context: Event.Context) : Event