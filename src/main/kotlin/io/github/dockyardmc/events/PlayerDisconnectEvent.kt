package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player

@EventDocumentation("when player disconnects", false)
class PlayerDisconnectEvent(val player: Player): Event {
    override val context = elements(player)
}