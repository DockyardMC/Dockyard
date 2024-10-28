package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player

@EventDocumentation("when player toggles flight", true)
class PlayerFlightToggleEvent(val player: Player, val flying: Boolean): CancellableEvent() {
    override val context = elements(player)
}