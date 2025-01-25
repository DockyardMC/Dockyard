package io.github.dockyardmc.protocol.plugin.messages

import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.configurations.ClientboundConfigurationPluginMessagePacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundPlayPluginMessagePacket
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

abstract class PluginMessageHandler {

    abstract fun handle(player: Player)
    abstract fun write(buffer: ByteBuf)

    fun asConfigPacket(channel: String): ClientboundConfigurationPluginMessagePacket {
        val data: ByteBuf = Unpooled.buffer()
        write(data)
        return ClientboundConfigurationPluginMessagePacket(channel, data)
    }

    fun asPlayPacket(channel: String): ClientboundPlayPluginMessagePacket {
        val data: ByteBuf = Unpooled.buffer()
        write(data)
        return ClientboundPlayPluginMessagePacket(channel, data)
    }
}