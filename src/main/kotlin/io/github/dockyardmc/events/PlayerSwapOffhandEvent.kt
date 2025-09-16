package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.Player

@EventDocumentation("when player swaps offhand items")
data class PlayerSwapOffhandEvent(val player: Player, var mainHandItem: ItemStack, var offHandItem: ItemStack, override val context: Event.Context): CancellableEvent()