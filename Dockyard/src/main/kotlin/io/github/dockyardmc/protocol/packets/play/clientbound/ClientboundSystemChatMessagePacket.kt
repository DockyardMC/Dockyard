package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.scroll.Component

@WikiVGEntry("System Chat Message")
@ClientboundPacketInfo(0x6C, ProtocolState.PLAY)
class ClientboundSystemChatMessagePacket(
    component: Component,
    isActionBar: Boolean,
): ClientboundPacket() {

    init {
        data.writeNBT(component.toNBT())
        data.writeBoolean(isActionBar)
    }
}