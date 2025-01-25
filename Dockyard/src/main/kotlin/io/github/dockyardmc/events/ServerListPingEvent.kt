package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.motd.ServerStatus
import io.github.dockyardmc.protocol.PlayerNetworkManager

@EventDocumentation("client requests motd/status", false)
class ServerListPingEvent(val playerNetworkManager: PlayerNetworkManager, var status: ServerStatus): Event {
    override val context = Event.Context(isGlobalEvent = true)
}