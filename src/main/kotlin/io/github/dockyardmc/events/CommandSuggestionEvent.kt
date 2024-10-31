package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player

@EventDocumentation("when client requests command suggestions", true)
class CommandSuggestionEvent(var command: String, val player: Player): CancellableEvent() {
    override val context = Event.Context(players = setOf(player))
}