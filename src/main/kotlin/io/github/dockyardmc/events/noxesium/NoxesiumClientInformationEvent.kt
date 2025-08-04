package io.github.dockyardmc.events.noxesium

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.events.Event
import io.github.dockyardmc.player.Player

@EventDocumentation("when server receives client information packet from noxesium")
class NoxesiumClientInformationEvent(val player: Player, val protocolVersion: Int, val versionString: String, override val context: Event.Context) : Event