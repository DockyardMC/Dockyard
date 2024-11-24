package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.item.writeItemStack
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundSetContainerSlotPacket(slot: Int, itemStack: ItemStack): ClientboundPacket() {

    init {
        data.writeVarInt(1)
        data.writeVarInt(0)
        data.writeShort(slot)
        data.writeItemStack(itemStack)
    }

}