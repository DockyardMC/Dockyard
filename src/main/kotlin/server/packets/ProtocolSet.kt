package cz.lukynka.dockyard.server.packets

import cz.lukynka.dockyard.server.packets.protocol.Packet
import io.netty.buffer.ByteBuf

class ProtocolSet(registrar: ProtocolSet.() -> Unit) {

    private val packets = mutableMapOf<Int, ProtocolPacket<*>>()
    init {
        registrar()
    }

    fun <T : Packet> registerPacket(id: Int, clazz: Class<T>, read: (ByteBuf) -> T) {
        packets[id] = ProtocolPacket(clazz, read)
    }

    fun getPacket(id: Int): ProtocolPacket<*>? {
        return packets[id]
    }

    class ProtocolPacket<T : Packet>(val clazz: Class<T>, val readFunction: (ByteBuf) -> T)
}