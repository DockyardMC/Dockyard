package io.github.dockyardmc.protocol.packets.configuration.clientbound

import io.github.dockyardmc.protocol.Packet
import io.github.dockyardmc.protocol.writers.readTextComponent
import io.github.dockyardmc.protocol.writers.writeTextComponent
import io.github.dockyardmc.scroll.Component
import io.netty.buffer.ByteBuf

class ClientboundConfigurationDisconnectPacket(val message: Component): Packet {

    override fun write(buffer: ByteBuf) {
        buffer.writeTextComponent(message)
    }

    companion object {
        fun read(buffer: ByteBuf): ClientboundConfigurationDisconnectPacket {
            return ClientboundConfigurationDisconnectPacket(buffer.readTextComponent())
        }
    }
}