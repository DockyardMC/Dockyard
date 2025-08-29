package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation

@EventDocumentation("when server ticks")
data class ServerTickEvent(val serverTicks: Long) : Event {
    override val context = Event.Context.GLOBAL
}