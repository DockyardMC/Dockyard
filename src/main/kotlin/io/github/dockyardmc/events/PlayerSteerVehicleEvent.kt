package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player

@EventDocumentation("when player steers a vehicle", false)
class PlayerSteerVehicleEvent(val player: Player, val vehicle: Entity, val location: Location, override val context: Event.Context) : Event {

}