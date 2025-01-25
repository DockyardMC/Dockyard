package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player

@EventDocumentation("when player enters the PLAY phase", false)
class PlayerJoinEvent(val player: Player): Event {
    override val context = Event.Context(players = setOf(player))
}