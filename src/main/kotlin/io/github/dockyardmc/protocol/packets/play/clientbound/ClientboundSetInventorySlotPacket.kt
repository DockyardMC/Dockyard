package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.item.writeItemStack
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import java.lang.IllegalArgumentException

@WikiVGEntry("Set Container Slot")
@ClientboundPacketInfo(0x15, ProtocolState.PLAY)
class ClientboundSetInventorySlotPacket(slot: Int, itemStack: ItemStack): ClientboundPacket() {

    init {
        DockyardServer.broadcastMessage("<yellow>$slot")
        if(slot < 0) throw IllegalArgumentException("Slot cannot be negative")
        data.writeVarInt(slot)
        data.writeItemStack(itemStack)
    }
}