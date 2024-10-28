package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.blocks.Block
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player

@EventDocumentation("when player breaks a block", true)
class PlayerBlockBreakEvent(val player: Player, var block: Block, var location: Location): CancellableEvent() {
    override val context = elements(player, block, location)
}