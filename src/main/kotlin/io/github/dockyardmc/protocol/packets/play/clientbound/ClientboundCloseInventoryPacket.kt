package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Close Container")
@ClientboundPacketInfo(0x0F, ProtocolState.PLAY)
class ClientboundCloseInventoryPacket(id: Int): ClientboundPacket() {

    init {
        data.writeByte(id)
    }

}