package io.github.dockyardmc.protocol.plugin.messages

import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.configurations.ClientboundPluginMessagePacket
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

abstract class PluginMessageHandler {

    abstract fun handle(player: Player)
    abstract fun write(buffer: ByteBuf)

    fun asPacket(channel: String): ClientboundPluginMessagePacket {
        val data: ByteBuf = Unpooled.buffer()
        write(data)
        return ClientboundPluginMessagePacket(channel, data)
    }
}