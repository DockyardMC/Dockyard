package io.github.dockyardmc.protocol.packets.login.clientbound

import io.github.dockyardmc.protocol.Packet
import io.github.dockyardmc.protocol.writers.readString
import io.github.dockyardmc.protocol.writers.writeString
import io.netty.buffer.ByteBuf

class ClientboundLoginCookieRequestPacket(val key: String): Packet {
    
    override fun write(buffer: ByteBuf) {
        buffer.writeString(key)
    }
    
    companion object {
        fun read(buffer: ByteBuf): ClientboundLoginCookieRequestPacket {
            return ClientboundLoginCookieRequestPacket(
                buffer.readString()
            )
        }
    }
}