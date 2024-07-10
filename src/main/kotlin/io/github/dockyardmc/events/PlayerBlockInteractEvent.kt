package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.Block

@EventDocumentation("when player interacts with block (right click)", false)
class PlayerBlockInteractEvent(val player: Player, var heldItem: ItemStack, var block: Block, var face: Direction, var location: Location): Event {
}