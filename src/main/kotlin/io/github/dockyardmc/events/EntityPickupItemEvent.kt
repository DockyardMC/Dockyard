package io.github.dockyardmc.events

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.item.ItemStack

class EntityPickupItemEvent(val entity: Entity, var item: ItemStack, override val context: Event.Context): CancellableEvent() {
}