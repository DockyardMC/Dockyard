package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.apis.bounds.Bound
import io.github.dockyardmc.player.Player

@EventDocumentation("When player leaves a bound")
class PlayerLeaveBoundEvent(val player: Player, val bound: Bound, override val context: Event.Context) : Event