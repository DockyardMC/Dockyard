package io.github.dockyardmc.protocol.packets.login

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.scroll.Component

@WikiVGEntry("Disconnect (login)")
@ClientboundPacketInfo(0x00, ProtocolState.LOGIN)
class ClientboundLoginDisconnectPacket(component: Component): ClientboundPacket() {

    init {
        data.writeString(component.toJson())
    }
}