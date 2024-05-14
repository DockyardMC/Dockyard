package io.github.dockyardmc.events

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.annotations.EventDocumentation

@EventDocumentation("server finishes loading", false)
class ServerFinishLoadEvent(server: DockyardServer): Event