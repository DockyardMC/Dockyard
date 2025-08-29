package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player

@EventDocumentation("when player toggles flight")
data class PlayerFlightToggleEvent(val player: Player, val flying: Boolean, override val context: Event.Context): CancellableEvent()