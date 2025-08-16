package io.github.dockyardmc.provider

import io.github.dockyardmc.protocol.packets.ClientboundPacket

interface PlayerPacketProvider : Provider {

    fun sendPacket(packet: ClientboundPacket) {
        playerGetter.forEach { player -> player.sendPacket(packet) }
    }

}