package io.github.dockyardmc.events

import io.github.dockyardmc.bindables.Bindable

class ServerListPingEvent(var message: Bindable<String>): Event {
}