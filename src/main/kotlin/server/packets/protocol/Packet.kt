package cz.lukynka.dockyard.server.packets.protocol

import cz.lukynka.dockyard.server.PacketHandler
import io.netty.buffer.ByteBuf

interface Packet {
    fun write(packet: ByteBuf) {}
    fun handle(handler: PacketHandler?) {}
}