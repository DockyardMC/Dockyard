package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation

@EventDocumentation("server sends the server brand to client during configuration", false)
class ServerBrandEvent(val brand: String): Event {
    override val context = elements()
}