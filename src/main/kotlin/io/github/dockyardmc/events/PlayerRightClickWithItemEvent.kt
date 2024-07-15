package io.github.dockyardmc.events

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.Player


class PlayerRightClickWithItemEvent(val player: Player, val item: ItemStack): CancellableEvent()