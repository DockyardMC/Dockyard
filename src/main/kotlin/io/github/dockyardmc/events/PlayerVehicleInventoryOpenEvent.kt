package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player

@EventDocumentation("when player opens vehicle's inventory while riding it")
data class PlayerVehicleInventoryOpenEvent(val player: Player, override val context: Event.Context) : CancellableEvent()