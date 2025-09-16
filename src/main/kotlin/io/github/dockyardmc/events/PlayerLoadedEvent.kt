package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player

@EventDocumentation("when player is fully loaded into a world")
data class PlayerLoadedEvent(val player: Player, override val context: Event.Context) : Event