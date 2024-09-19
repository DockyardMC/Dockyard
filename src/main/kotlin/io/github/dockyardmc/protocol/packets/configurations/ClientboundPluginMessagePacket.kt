package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.netty.buffer.ByteBuf

@WikiVGEntry("Clientbound Plugin Message (configuration)")
@ClientboundPacketInfo(0x01, ProtocolState.CONFIGURATION)
class ClientboundPluginMessagePacket(
    channel: String,
    payload: ByteBuf,
): ClientboundPacket() {

    init {
        data.writeUtf(channel)
        data.writeBytes(payload)
    }
}