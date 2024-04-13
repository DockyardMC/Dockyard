package io.github.dockyard.server.packets.protocol

import io.github.dockyard.server.PacketHandler
import io.netty.buffer.ByteBuf

interface Packet {
    fun write(packet: ByteBuf) {}
    fun handle(handler: PacketHandler?) {}
}