package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.apis.bounds.Bound
import io.github.dockyardmc.player.Player

@EventDocumentation("when player enters a bound")
data class PlayerEnterBoundEvent(val player: Player, val bound: Bound, override val context: Event.Context) : Event