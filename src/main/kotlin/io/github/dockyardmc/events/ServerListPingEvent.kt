package io.github.dockyardmc.events

import io.github.dockyardmc.motd.ServerStatus

class ServerListPingEvent(var status: ServerStatus): Event {
}