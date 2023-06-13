package cz.lukynka.dockyard.server.packets.protocol

import cz.lukynka.dockyard.exceptions.InvalidPacketException
import cz.lukynka.dockyard.server.packets.Protocol
import cz.lukynka.dockyard.server.packets.readVarInt
import io.netty.buffer.ByteBuf

class PacketWrapper(protocol: Protocol, buf: ByteBuf) {
    var length: Int = buf.readVarInt()
    var packetId: Int = buf.readVarInt()
    var data = readPacket(protocol, buf)

    private fun readPacket(protocol: Protocol, buf: ByteBuf): Packet {
        val packet = protocol.set.getPacket(packetId) ?: throw InvalidPacketException("Packet ID '$packetId' is not valid.")
        return packet.readFunction(buf)
    }
}