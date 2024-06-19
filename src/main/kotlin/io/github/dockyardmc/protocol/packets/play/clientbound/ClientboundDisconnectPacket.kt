package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.scroll.Component

@WikiVGEntry("Disconnect (play)")
@ClientboundPacketInfo(0x1D, ProtocolState.PLAY)
class ClientboundDisconnectPacket(reason: Component): ClientboundPacket() {

    init {
        data.writeNBT(reason.toNBT())
    }

}