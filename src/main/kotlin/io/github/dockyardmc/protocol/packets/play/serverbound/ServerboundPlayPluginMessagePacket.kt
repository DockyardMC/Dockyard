package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.events.PluginMessageReceivedEvent
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.protocol.plugin.PluginMessages
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Serverbound Plugin Message (play)")
@ServerboundPacketInfo(0x12, ProtocolState.PLAY)
class ServerboundPlayPluginMessagePacket(val channel: String, val data: ByteBuf): ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        val event = PluginMessageReceivedEvent(processor.player, channel, data)
        if(event.cancelled) return
        PluginMessages.handle(channel, data, processor.player)
    }

    companion object {
        fun read(byteBuf: ByteBuf): ServerboundPlayPluginMessagePacket {
            val channel = byteBuf.readString(32767)
            val data = byteBuf.readBytes(byteBuf.readableBytes())
            return ServerboundPlayPluginMessagePacket(channel, data)
        }
    }
}