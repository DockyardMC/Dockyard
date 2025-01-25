package io.github.dockyardmc.protocol.packets.login

import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.scroll.extensions.toComponent

class ClientboundLoginDisconnectPacket(reason: String): ClientboundPacket() {

    init {
        data.writeString(reason.toComponent().toJson())
    }
}