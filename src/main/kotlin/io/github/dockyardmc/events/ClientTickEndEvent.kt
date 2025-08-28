package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player

@EventDocumentation("when the current tick of client ends executing")
data class ClientTickEndEvent(val player: Player, override val context: Event.Context): Event