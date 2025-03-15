package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundSetInventorySlotPacket(slot: Int, itemStack: ItemStack) : ClientboundPacket() {

    init {
        if (slot < 0) throw IllegalArgumentException("Slot cannot be negative")
        buffer.writeVarInt(slot)
        itemStack.write(buffer)
    }
}