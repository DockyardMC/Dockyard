package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player

@EventDocumentation("when player accepts the server code of conduct")
data class PlayerAcceptCodeOfConductEvent(val player: Player, override val context: Event.Context) : Event