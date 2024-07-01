package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.scroll.Component

@WikiVGEntry("Set Subtitle Text")
@ClientboundPacketInfo(0x63, ProtocolState.PLAY)
class ClientboundSubtitlePacket(component: Component): ClientboundPacket() {
    init {
        data.writeNBT(component.toNBT())
    }
}