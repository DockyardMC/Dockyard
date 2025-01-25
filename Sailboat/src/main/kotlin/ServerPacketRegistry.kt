package io.github.dockyardmc.sailboat

import io.github.dockyardmc.protocol.packets.handshake.serverbound.ServerboundHandshakePacket
import io.github.dockyardmc.socket.PacketRegistry

object ServerPacketRegistry: PacketRegistry() {

    override fun load() {
        addHandshake(ServerboundHandshakePacket::class, ::handleHandshake)
    }

}