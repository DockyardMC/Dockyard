package io.github.dockyardmc.events

import io.github.dockyardmc.player.Player

data class ServerSendPlayerMessageEvent(val player: Player, val message: String, val isSystem: Boolean, override val context: Event.Context) : CancellableEvent()