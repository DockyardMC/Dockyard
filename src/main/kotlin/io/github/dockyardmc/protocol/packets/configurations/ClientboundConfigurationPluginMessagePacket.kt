package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.netty.buffer.ByteBuf

class ClientboundConfigurationPluginMessagePacket(
    channel: String,
    payload: ByteBuf,
): ClientboundPacket() {

    init {
        data.writeString(channel)
        data.writeBytes(payload)
    }
}