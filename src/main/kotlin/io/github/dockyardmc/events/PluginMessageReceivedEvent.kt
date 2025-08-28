package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player
import io.netty.buffer.ByteBuf

@EventDocumentation("server receives plugin message from client (Custom Payload Packet)")
data class PluginMessageReceivedEvent(val player: Player, var channel: String, var data: ByteBuf, override val context: Event.Context) : CancellableEvent()