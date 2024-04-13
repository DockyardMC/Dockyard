package io.github.dockyard.server.packets

import io.github.dockyard.server.packets.protocol.handshake.ServerboundHandshakePacket
import io.github.dockyard.server.packets.protocol.status.ServerboundStatusRequestPacket

enum class Protocol(val registrar: ProtocolSet.() -> Unit) {

    HANDSHAKE({
        this.registerPacket(0, ServerboundHandshakePacket::class.java, ServerboundHandshakePacket::read)
    }),
    STATUS({
        this.registerPacket(0, ServerboundStatusRequestPacket::class.java) { ServerboundStatusRequestPacket() }
    });

    val set = ProtocolSet(registrar)

}