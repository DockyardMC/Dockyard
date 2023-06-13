package cz.lukynka.dockyard.server

import cz.lukynka.dockyard.server.packets.Protocol
import cz.lukynka.dockyard.server.packets.protocol.handshake.ServerboundHandshakePacket
import cz.lukynka.dockyard.server.packets.protocol.status.ServerboundStatusRequestPacket

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