package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.entities.Entity

@EventDocumentation("when player tries to mount an vehicle", true)
class EntityRideVehicleEvent(val vehicle: Entity, val passenger: Entity, override val context: Event.Context): CancellableEvent() {
}