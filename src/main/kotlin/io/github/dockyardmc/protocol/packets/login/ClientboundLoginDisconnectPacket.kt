package io.github.dockyardmc.protocol.packets.login

import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.scroll.Component

@WikiVGEntry("Disconnect (login)")
class ClientboundLoginDisconnectPacket(component: Component): ClientboundPacket(0x00, ProtocolState.LOGIN) {

    init {
        data.writeUtf(component.toJson())
    }
}