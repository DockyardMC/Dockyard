package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.motd.ServerStatus

@EventDocumentation("client requests motd/status", false)
class ServerListPingEvent(var status: ServerStatus): Event {
    override val context = elements()
}