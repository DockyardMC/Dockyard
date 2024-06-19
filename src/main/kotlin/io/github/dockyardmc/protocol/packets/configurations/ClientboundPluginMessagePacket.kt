package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Clientbound Plugin Message (configuration)")
@ClientboundPacketInfo(0x01, ProtocolState.CONFIGURATION)
class ClientboundPluginMessagePacket(
    channel: String,
    messageData: String,
): ClientboundPacket() {

    init {
        data.writeUtf(channel)
        data.writeUtf(messageData)
    }
}