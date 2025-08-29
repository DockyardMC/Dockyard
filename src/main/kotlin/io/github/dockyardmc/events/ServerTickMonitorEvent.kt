package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation

@EventDocumentation("when tick along with its profiler ends. Only use this for monitoring")
data class ServerTickMonitorEvent(val tickTime: Long, val ticks: Long) : Event {
    override val context: Event.Context = Event.Context.GLOBAL
}