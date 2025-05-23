package io.github.dockyardmc.ui.new

import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundOpenContainerPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundSetContainerSlotPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.InventoryType
import io.github.dockyardmc.ui.DrawableClickType

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

        if (clickedSlot == null) {
            update(player)
            return
        }

        try {
            clickedSlot.onClick?.invoke(player, clickType) // execution stops here?? no exception as well
        } catch (ex: Throwable) {
            ex.printStackTrace()
            throw ex
        }

        update(player)
    }

    override fun dispose() {
        getChildren().forEach { child ->
            child.key.dispose()
        }
    }
}