package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundSetContainerSlotPacket(slot: Int, itemStack: ItemStack): ClientboundPacket() {

    init {
        buffer.writeVarInt(1)
        buffer.writeVarInt(0)
        buffer.writeShort(slot)
        itemStack.write(buffer)
    }
}