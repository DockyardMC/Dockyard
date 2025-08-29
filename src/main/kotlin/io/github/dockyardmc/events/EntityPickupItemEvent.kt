package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.ItemDropEntity

@EventDocumentation("when entity pickups dropped item")
data class EntityPickupItemEvent(val entity: Entity, var itemDropEntity: ItemDropEntity, override val context: Event.Context): CancellableEvent()