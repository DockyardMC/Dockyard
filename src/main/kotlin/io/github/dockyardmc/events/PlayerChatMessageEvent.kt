package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player

@EventDocumentation("when player sends a chat message")
data class PlayerChatMessageEvent(var message: String, val player: Player, override val context: Event.Context) : CancellableEvent()