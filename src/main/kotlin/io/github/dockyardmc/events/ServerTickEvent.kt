package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation

@EventDocumentation("when server ticks", false)
class ServerTickEvent(val serverTicks: Long): Event {
    override val context = Event.Context(isGlobalEvent = true)
}