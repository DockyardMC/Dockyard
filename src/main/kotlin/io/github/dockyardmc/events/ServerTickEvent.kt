package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation

@EventDocumentation("when server ticks", false)
class ServerTickEvent(val serverTicks: Int): Event {
    override val context = elements()
}