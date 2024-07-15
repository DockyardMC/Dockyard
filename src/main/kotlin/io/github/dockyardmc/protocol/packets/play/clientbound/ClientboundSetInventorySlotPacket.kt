package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.item.writeItemStack
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Set Container Slot")
@ClientboundPacketInfo(0x15, ProtocolState.PLAY)
class ClientboundSetInventorySlotPacket(windowId: Int, stateId: Int, slot: Int, itemStack: ItemStack): ClientboundPacket() {

    init {
        data.writeByte(windowId)
        data.writeVarInt(stateId)
        data.writeShort(slot)
        data.writeItemStack(itemStack)
    }
}