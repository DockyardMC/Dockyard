package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.types.EquipmentSlot

@EventDocumentation("when player equips piece of equipment")
data class PlayerEquipEvent(
    val player: Player,
    var item: ItemStack,
    val slot: EquipmentSlot,
    override val context: Event.Context
) : Event