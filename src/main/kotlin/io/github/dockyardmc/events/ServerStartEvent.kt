package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation

@EventDocumentation("server starts (before loading starts)")
class ServerStartEvent() : Event {
    override val context = Event.Context.GLOBAL
}