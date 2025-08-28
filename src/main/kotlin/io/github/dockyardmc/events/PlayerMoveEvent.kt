package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player

@EventDocumentation("player moves or rotates their head")
data class PlayerMoveEvent(var oldLocation: Location, var newLocation: Location, var player: Player, var isOnlyHeadMovement: Boolean, override val context: Event.Context) : CancellableEvent()