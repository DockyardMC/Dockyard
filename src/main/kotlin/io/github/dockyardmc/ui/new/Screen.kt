package io.github.dockyardmc.ui.new

import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundOpenContainerPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundSetContainerSlotPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.InventoryType
import io.github.dockyardmc.ui.DrawableClickType
import io.github.dockyardmc.utils.debug

abstract class Screen : CompositeDrawable() {

    fun open(player: Player) {
        player.sendPacket(ClientboundOpenContainerPacket(InventoryType.GENERIC_9X6, "screen"))
        player.currentlyOpenScreen = this
        onRenderInternal()
        update(player)
    }

    fun update(player: Player) {
        getSlots().forEach { (slot, item) ->
            player.sendPacket(ClientboundSetContainerSlotPacket(slot, item.itemStack))
        }
    }

    fun onClick(slot: Int, player: Player, clickType: DrawableClickType) {
        val clickedSlot = getSlots()[slot]
        debug("<white>Clicked <yellow>$slot <lime>(${clickType.name})", true)

        if (clickedSlot == null) {
            update(player)
            return
        }

        clickedSlot.onClick?.invoke(player, clickType)
        update(player)
    }
}