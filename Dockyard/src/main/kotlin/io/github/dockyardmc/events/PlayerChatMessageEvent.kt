package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player

@EventDocumentation("when player sends a chat message", true)
class PlayerChatMessageEvent(var message: String, val player: Player): CancellableEvent() {
    override val context = Event.Context(players = setOf(player))
}