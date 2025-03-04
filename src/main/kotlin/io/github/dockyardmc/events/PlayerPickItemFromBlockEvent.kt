package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player

@EventDocumentation("when player picks a item from block", true)
class PlayerPickItemFromBlockEvent(
    val player: Player,
    val blockLocation: Location,
    var block: io.github.dockyardmc.world.block.Block,
    val includeData: Boolean,
    override val context: Event.Context
) : CancellableEvent()