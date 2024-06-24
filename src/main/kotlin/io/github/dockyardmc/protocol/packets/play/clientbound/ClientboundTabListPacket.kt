package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.scroll.Component

@WikiVGEntry("Set Tab List Header And Footer")
@ClientboundPacketInfo(0x6D, ProtocolState.PLAY)
class ClientboundTabListPacket(
    header: Component,
    footer: Component
): ClientboundPacket() {
    init {
        data.writeNBT(header.toNBT())
        data.writeNBT(footer.toNBT())
    }
}