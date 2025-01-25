package io.github.dockyardmc.protocol.packets.configuration.clientbound

import io.github.dockyardmc.protocol.Packet
import io.github.dockyardmc.protocol.writers.readString
import io.github.dockyardmc.protocol.writers.readVarInt
import io.github.dockyardmc.protocol.writers.writeString
import io.github.dockyardmc.protocol.writers.writeVarInt
import io.netty.buffer.ByteBuf

// omg TRANSfer packet?? 🏳️‍⚧️🏳️‍⚧️🏳️‍⚧️🏳️‍⚧️🏳️‍⚧️
class ClientboundConfigurationTransferPacket(val host: String, val port: Int): Packet {

    override fun write(buffer: ByteBuf) {
        buffer.writeString(host)
        buffer.writeVarInt(port)
    }

    companion object {
        fun read(buffer: ByteBuf): ClientboundConfigurationTransferPacket {
            return ClientboundConfigurationTransferPacket(
                buffer.readString(),
                buffer.readVarInt()
            )
        }
    }

}