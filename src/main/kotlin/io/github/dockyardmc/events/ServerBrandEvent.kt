package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation

@EventDocumentation("server sends the server brand to client during configuration")
data class ServerBrandEvent(val brand: String, override val context: Event.Context) : Event