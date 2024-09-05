package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.Player

@EventDocumentation("when player drops item", true)
class PlayerDropItemEvent(val player: Player, var itemStack: ItemStack): CancellableEvent() {
}