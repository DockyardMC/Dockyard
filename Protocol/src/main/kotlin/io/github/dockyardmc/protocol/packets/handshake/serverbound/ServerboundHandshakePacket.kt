package io.github.dockyardmc.protocol.packets.handshake.serverbound

import io.github.dockyardmc.protocol.Packet
import io.github.dockyardmc.protocol.ProtocolState
import io.github.dockyardmc.protocol.writers.*
import io.netty.buffer.ByteBuf

class ServerboundHandshakePacket(
    val version: Int,
    val serverAddress: String,
    val port: Short,
    val nextState: ProtocolState,
) : Packet {

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(version)
        buffer.writeString(serverAddress)
        buffer.writeShort(port)
        buffer.writeEnum<ProtocolState>(nextState)
    }

    companion object {
        fun read(buffer: ByteBuf): ServerboundHandshakePacket {
            return ServerboundHandshakePacket(
                buffer.readVarInt(),
                buffer.readString(),
                buffer.readShort(),
                buffer.readEnum<ProtocolState>()
            )
        }
    }
}