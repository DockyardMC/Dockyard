package cz.lukynka.dockyard.server.packets

import io.netty.buffer.ByteBuf

interface Packet {
    fun read(packet: ByteBuf) {

    }

    fun write(packet: ByteBuf) {

    }
}