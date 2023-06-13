package cz.lukynka.dockyard.server.packets.protocol.status

import cz.lukynka.dockyard.server.PacketHandler
import cz.lukynka.dockyard.server.packets.protocol.Packet

class ServerboundStatusRequestPacket : Packet {
    override fun handle(handler: PacketHandler?) {
        handler?.handleStatusRequest(this)
    }
}