package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundPluginMessagePacket(channel: String, messageData: String): ClientboundPacket(0) {

    init {
        data.writeUtf(channel)
        data.writeUtf(messageData)
    }
}