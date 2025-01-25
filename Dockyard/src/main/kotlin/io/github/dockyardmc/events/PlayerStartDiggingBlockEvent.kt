package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.blocks.Block
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player

@EventDocumentation("when player starts digging a block", false)
class PlayerStartDiggingBlockEvent(val player: Player, val location: Location, val block: Block, override val context: Event.Context) : Event {
}