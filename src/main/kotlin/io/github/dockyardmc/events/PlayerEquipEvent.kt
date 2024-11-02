package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.item.EquipmentSlot
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.Player

@EventDocumentation("player equips piece of equipment", false)
class PlayerEquipEvent(val player: Player, var item: ItemStack, val slot: EquipmentSlot): Event