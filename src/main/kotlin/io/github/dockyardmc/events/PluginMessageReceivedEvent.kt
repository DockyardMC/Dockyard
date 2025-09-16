package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.plugin.messages.PluginMessage

@EventDocumentation("server receives plugin message from client (Custom Payload Packet)")
data class PluginMessageReceivedEvent(val player: Player, val contents: PluginMessage.Contents, override val context: Event.Context) : CancellableEvent()