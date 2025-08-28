package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player

@EventDocumentation("when player leaves the server (during PLAY phase)")
data class PlayerLeaveEvent(val player: Player, override val context: Event.Context) : Event