package io.github.dockyardmc.protocol.packets.login

import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.netty.buffer.ByteBuf

data class ClientboundLoginPluginRequestPacket(val messageId: Int, val channel: String, val data: ByteBuf) : ClientboundPacket() {

    init {
        buffer.writeVarInt(messageId)
        buffer.writeString(channel)
        buffer.writeBytes(data)
    }
}