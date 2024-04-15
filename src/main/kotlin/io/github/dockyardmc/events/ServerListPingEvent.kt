package io.github.dockyardmc.events

import io.github.dockyardmc.bindables.Bindable
import io.github.dockyardmc.motd.ServerStatus

class ServerListPingEvent(var status: Bindable<ServerStatus>): Event {
}