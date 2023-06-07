package cz.lukynka.dockyard.Server.Packets

import io.netty.buffer.ByteBuf

class PacketWrapper(packet: ByteBuf) {
    var length: Int = packet.readInt()
    var packetId: Int = packet.readInt()
    var data: ByteBuf = packet.readBytes();
}