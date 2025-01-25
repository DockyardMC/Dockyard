package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.protocol.packets.ProtocolState

@EventDocumentation("when server receives handshake packet", true)
data class ServerHandshakeEvent(var version: Int, var serverAddress: String, var port: Short, var nextState: ProtocolState, override val context: Event.Context): CancellableEvent()