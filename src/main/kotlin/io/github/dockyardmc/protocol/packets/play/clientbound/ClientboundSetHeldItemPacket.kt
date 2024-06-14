package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Set Held Item (clientbound)")
class ClientboundSetHeldItemPacket(slot: Int): ClientboundPacket(0x53, ProtocolState.PLAY) {

    init {
        data.writeByte(slot)
    }

}