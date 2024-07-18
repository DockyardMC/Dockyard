package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Set Ticking State")
@ClientboundPacketInfo(0x71, ProtocolState.PLAY)
class ClientboundSetTickingStatePacket(
    tickRate: Int,
    isFrozen: Boolean,
): ClientboundPacket() {

    init {
        data.writeFloat(tickRate.toFloat())
        data.writeBoolean(isFrozen)
    }

}