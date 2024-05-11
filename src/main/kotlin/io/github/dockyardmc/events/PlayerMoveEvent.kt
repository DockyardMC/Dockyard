package io.github.dockyardmc.events

import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player

class PlayerMoveEvent(var oldLocation: Location, var newLocation: Location, var player: Player, var isOnlyHeadMovement: Boolean): Event