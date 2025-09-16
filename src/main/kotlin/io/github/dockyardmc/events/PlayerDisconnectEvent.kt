package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player

@EventDocumentation("when player disconnects")
data class PlayerDisconnectEvent(val player: Player, override val context: Event.Context) : Event