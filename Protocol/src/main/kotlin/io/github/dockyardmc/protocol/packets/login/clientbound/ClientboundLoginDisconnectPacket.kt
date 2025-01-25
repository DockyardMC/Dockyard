package io.github.dockyardmc.protocol.packets.login.clientbound

import io.github.dockyardmc.protocol.Packet
import io.github.dockyardmc.protocol.writers.readTextComponent
import io.github.dockyardmc.protocol.writers.writeTextComponent
import io.github.dockyardmc.scroll.Component
import io.netty.buffer.ByteBuf

class ClientboundLoginDisconnectPacket(val reason: Component): Packet {

    override fun write(buffer: ByteBuf) {
        buffer.writeTextComponent(reason)
    }

    companion object {
        fun read(buffer: ByteBuf): ClientboundLoginDisconnectPacket {
            return ClientboundLoginDisconnectPacket(buffer.readTextComponent())
        }
    }
}