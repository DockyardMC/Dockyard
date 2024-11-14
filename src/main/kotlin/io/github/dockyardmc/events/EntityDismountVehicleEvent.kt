package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.entities.Entity

@EventDocumentation("when player tries to dismount from a vehicle", true)
class EntityDismountVehicleEvent(val vehicle: Entity, val passenger: Entity, override val context: Event.Context): CancellableEvent() {
}