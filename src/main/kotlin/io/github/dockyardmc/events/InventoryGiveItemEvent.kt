package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.item.ItemStack

@EventDocumentation("when item is given to an entity inventory")
data class InventoryGiveItemEvent(val entity: Entity, val item: ItemStack, val success: Boolean, override val context: Event.Context) : Event