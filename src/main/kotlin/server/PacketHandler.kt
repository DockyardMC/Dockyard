package io.github.dockyard.server

import io.github.dockyard.server.packets.Protocol
import io.github.dockyard.server.packets.protocol.handshake.ServerboundHandshakePacket
import io.github.dockyard.server.packets.protocol.status.ServerboundStatusRequestPacket

class PacketHandler(val channel: PacketProcessingHandler) {
    var protocol: Protocol = Protocol.HANDSHAKE

    fun handleHandshake(packet: ServerboundHandshakePacket) {
        println("Got handshake packet with version ${packet.protocolVersion}")
        protocol = Protocol.STATUS
    }

    fun handleStatusRequest(packet: ServerboundStatusRequestPacket) {
        println("Got Status Request Packet!")
    }
}