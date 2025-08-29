package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.world.World
import io.github.dockyardmc.world.block.Block

@EventDocumentation("when player finished placing block. This event is mainly used for testing")
data class PlayerFinishPlacingBlockEvent(val player: Player, val world: World, val block: Block, val location: Location, override val context: Event.Context): Event