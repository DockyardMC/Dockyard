package io.github.dockyardmc.protocol.packets.login

import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.scroll.Component

class ClientboundLoginDisconnectPacket(component: Component): ClientboundPacket(0x00) {

    init {
        data.writeUtf(component.toJson())
    }

}