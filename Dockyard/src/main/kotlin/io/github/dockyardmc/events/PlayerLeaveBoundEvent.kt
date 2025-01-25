package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.bounds.Bound
import io.github.dockyardmc.player.Player

@EventDocumentation("When player leaves a bound", false)
class PlayerLeaveBoundEvent(val player: Player, val bound: Bound): Event {
    override val context = Event.Context(players = setOf(player), worlds = setOf(bound.world))
}