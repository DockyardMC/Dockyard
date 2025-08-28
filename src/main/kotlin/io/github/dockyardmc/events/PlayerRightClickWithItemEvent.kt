package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.Player


@EventDocumentation("when player right clicks with item in hand")
data class PlayerRightClickWithItemEvent(val player: Player, val item: ItemStack, override val context: Event.Context) : CancellableEvent()