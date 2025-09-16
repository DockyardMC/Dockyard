package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.protocol.packets.handshake.ServerboundHandshakePacket

@EventDocumentation("when server receives handshake packet")
data class ServerHandshakeEvent(var version: Int, var serverAddress: String, var port: Short, var intent: ServerboundHandshakePacket.Intent, override val context: Event.Context) : CancellableEvent()