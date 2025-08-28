package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.Player

@EventDocumentation("when player drops item")
data class PlayerDropItemEvent(val player: Player, var itemStack: ItemStack, override val context: Event.Context) : CancellableEvent()