package io.github.dockyardmc.provider

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.plugin.messages.PluginMessage

interface PlayerPacketProvider : Provider {

    fun sendPacket(packet: ClientboundPacket) {
        playerGetter.forEach { player -> player.sendPacket(packet) }
    }

    fun sendPluginMessage(pluginMessage: PluginMessage) {
        playerGetter.forEach { player -> player.se }
    }

}