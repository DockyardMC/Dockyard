package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.world.block.Block

@EventDocumentation("when player interacts with block")
data class PlayerBlockRightClickEvent(val player: Player, var heldItem: ItemStack, var block: Block, var face: Direction, var location: Location, override val context: Event.Context) : CancellableEvent()