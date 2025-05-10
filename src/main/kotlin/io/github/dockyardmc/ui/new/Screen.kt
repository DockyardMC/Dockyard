package io.github.dockyardmc.ui.new

import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundOpenContainerPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundSetContainerSlotPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.InventoryType
import io.github.dockyardmc.utils.debug

abstract class Screen : CompositeDrawable() {

    fun open(player: Player) {
        player.sendPacket(ClientboundOpenContainerPacket(InventoryType.GENERIC_9X6, "screen"))
    }

    fun render(player: Player) {

        onRenderInternal()
        getSlots().forEach { (slot, item) ->
            debug("$slot -> ${item.itemStack.material.identifier}", true)
            player.sendPacket(ClientboundSetContainerSlotPacket(slot, item.itemStack))

        }
    }

}