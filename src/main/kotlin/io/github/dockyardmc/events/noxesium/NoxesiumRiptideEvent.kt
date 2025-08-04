package io.github.dockyardmc.events.noxesium

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.events.Event
import io.github.dockyardmc.player.Player

@EventDocumentation("when noxesium sends riptide packet")
data class NoxesiumRiptideEvent(val player: Player, val slot: Int, override val context: Event.Context) : Event