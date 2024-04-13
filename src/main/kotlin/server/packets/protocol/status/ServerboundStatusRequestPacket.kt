package io.github.dockyard.server.packets.protocol.status

import io.github.dockyard.server.PacketHandler
import io.github.dockyard.server.packets.protocol.Packet

class ServerboundStatusRequestPacket : Packet {
    override fun handle(handler: PacketHandler?) {
        handler?.handleStatusRequest(this)
    }
}