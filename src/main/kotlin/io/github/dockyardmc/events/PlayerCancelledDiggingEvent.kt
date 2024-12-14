package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.blocks.Block
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player

@EventDocumentation("when player cancels digging of a block", false)
class PlayerCancelledDiggingEvent(val player: Player, val location: Location, val block: Block, override val context: Event.Context): Event