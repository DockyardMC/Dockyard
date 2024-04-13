package io.github.dockyard.server.packets.protocol.handshake

import io.github.dockyard.server.PacketHandler
import io.github.dockyard.server.packets.protocol.Packet
import io.github.dockyard.server.packets.readUtf
import io.github.dockyard.server.packets.readVarInt
import io.netty.buffer.ByteBuf

class ServerboundHandshakePacket(
    val protocolVersion: Int,
    val serverAddress: String,
    val port: Int,
    val nextState: Int
) : Packet {
    override fun handle(handler: PacketHandler?) {
        handler?.handleHandshake(this)
    }

    companion object {
        fun read(byteBuf: ByteBuf): ServerboundHandshakePacket {
            return ServerboundHandshakePacket(
                protocolVersion = byteBuf.readVarInt(),
                serverAddress = byteBuf.readUtf(),
                port = byteBuf.readUnsignedShort(),
                nextState = byteBuf.readVarInt()
            )
        }
    }
}