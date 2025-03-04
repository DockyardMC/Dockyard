package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player

@EventDocumentation("when player finishes digging a block", false)
class PlayerFinishedDiggingEvent(val player: Player, val location: Location, val block: io.github.dockyardmc.world.block.Block, override val context: Event.Context): Event