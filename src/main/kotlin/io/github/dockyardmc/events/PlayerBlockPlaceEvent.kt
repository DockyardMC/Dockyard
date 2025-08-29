package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.world.block.Block

@EventDocumentation("when player places a block")
class PlayerBlockPlaceEvent(val player: Player, var block: Block, var location: Location, override val context: Event.Context) : CancellableEvent()