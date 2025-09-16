package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player

@EventDocumentation("when server sends player a chat message")
data class ServerSendPlayerMessageEvent(val player: Player, val message: String, val isSystem: Boolean, override val context: Event.Context) : CancellableEvent()