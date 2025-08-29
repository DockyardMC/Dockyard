package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player

@EventDocumentation("when player respawns after dying")
data class PlayerRespawnEvent(val player: Player, val isBecauseOfDeath: Boolean, override val context: Event.Context): Event