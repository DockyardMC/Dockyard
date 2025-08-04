package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.Player

@EventDocumentation("every tick when player is consuming item", false)
data class PlayerConsumeItemTickEvent(val player: Player, val item: ItemStack, val tick: Int, override val context: Event.Context) : Event