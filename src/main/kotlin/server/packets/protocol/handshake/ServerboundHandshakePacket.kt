package cz.lukynka.dockyard.server.packets.protocol.handshake

import cz.lukynka.dockyard.server.PacketHandler
import cz.lukynka.dockyard.server.packets.protocol.Packet
import cz.lukynka.dockyard.server.packets.readUtf
import cz.lukynka.dockyard.server.packets.readVarInt
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