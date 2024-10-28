package io.github.dockyardmc.events

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.annotations.EventDocumentation

@EventDocumentation("server finishes loading", false)
class ServerFinishLoadEvent(val server: DockyardServer): Event {
    override val context = elements()
}