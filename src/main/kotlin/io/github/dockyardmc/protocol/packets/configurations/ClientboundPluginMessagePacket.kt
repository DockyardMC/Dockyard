package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Clientbound Plugin Message (configuration)")
class ClientboundPluginMessagePacket(
    channel: String,
    messageData: String,
): ClientboundPacket(0x01, ProtocolState.CONFIGURATION) {

    init {
        data.writeUtf(channel)
        data.writeUtf(messageData)
    }
}