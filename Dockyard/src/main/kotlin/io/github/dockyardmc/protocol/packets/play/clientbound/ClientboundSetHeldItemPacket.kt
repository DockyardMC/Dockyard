package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Set Held Item (clientbound)")
@ClientboundPacketInfo(0x53, ProtocolState.PLAY)
class ClientboundSetHeldItemPacket(slot: Int): ClientboundPacket() {

    init {
        data.writeByte(slot)
    }

}