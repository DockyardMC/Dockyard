package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.bounds.Bound
import io.github.dockyardmc.player.Player

@EventDocumentation("when player enters a bound", true)
class PlayerEnterBoundEvent(val player: Player, val bound: Bound): CancellableEvent() {

}