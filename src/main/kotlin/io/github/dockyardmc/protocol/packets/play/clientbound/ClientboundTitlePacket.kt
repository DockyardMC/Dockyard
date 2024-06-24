package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.scroll.Component

@WikiVGEntry("Set Title Text")
@ClientboundPacketInfo(0x65, ProtocolState.PLAY)
class ClientboundSetTitlePacket(val component: Component): ClientboundPacket() {
    init {
        data.writeNBT(component.toNBT())
    }
}