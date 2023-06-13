package cz.lukynka.dockyard.server.packets

import cz.lukynka.dockyard.server.packets.protocol.handshake.ServerboundHandshakePacket
import cz.lukynka.dockyard.server.packets.protocol.status.ServerboundStatusRequestPacket

enum class Protocol(val registrar: ProtocolSet.() -> Unit) {

    HANDSHAKE({
        this.registerPacket(0, ServerboundHandshakePacket::class.java, ServerboundHandshakePacket::read)
    }),
    STATUS({
        this.registerPacket(0, ServerboundStatusRequestPacket::class.java) { ServerboundStatusRequestPacket() }
    });

    val set = ProtocolSet(registrar)

}