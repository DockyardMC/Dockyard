package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.netty.buffer.ByteBuf

class ClientboundPlayPluginMessagePacket(
    channel: String,
    payload: ByteBuf,
) : ClientboundPacket() {

    init {
        buffer.writeString(channel)
        buffer.writeBytes(payload)
    }
}