package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeItemStackList
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.item.writeItemStack
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Set Container Content")
@ClientboundPacketInfo(0x13, ProtocolState.PLAY)
class ClientboundSetContainerContentPacket(player: Player): ClientboundPacket() {

    init {
        data.writeByte(0)
        data.writeVarInt(0)
        data.writeItemStackList(player.inventory.slots.values.values.reversed())
        data.writeItemStack(player.inventory.carriedItem)
    }
}