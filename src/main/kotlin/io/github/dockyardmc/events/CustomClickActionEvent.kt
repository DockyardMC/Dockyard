package io.github.dockyardmc.events

import io.github.dockyardmc.player.Player

class CustomClickActionEvent(player: Player, val id: String, val payload: String?) : Event {
    override val context: Event.Context = Event.Context(players = setOf(player))
}