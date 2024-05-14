package io.github.dockyardmc.events

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.annotations.EventDocumentation

@EventDocumentation("server starts (before loading starts)", false)
class ServerStartEvent(server: DockyardServer): Event