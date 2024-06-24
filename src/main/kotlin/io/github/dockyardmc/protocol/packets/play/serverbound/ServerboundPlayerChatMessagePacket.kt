package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerChatMessageEvent
import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Chat Message")
@ServerboundPacketInfo(6, ProtocolState.PLAY)
class ServerboundPlayerChatMessagePacket(var message: String): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        val event = PlayerChatMessageEvent(message, processor.player)
        Events.dispatch(event)
        if (event.cancelled) return

        // TODO: add prefix and suffix to chat
        DockyardServer.broadcastMessage("<white>${event.player}: <white>${event.message}")
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundPlayerChatMessagePacket {
            val text = buf.readUtf(256)
            val timestamp = buf.readInstant()
            val salt = buf.readLong()
            val hasSignature = buf.readBoolean()
            if(hasSignature) {
                buf.readBytes(256)
            }
            val msgCount = buf.readVarInt()
            val ack = buf.readFixedBitSet(20)

            return ServerboundPlayerChatMessagePacket(text)
        }
    }
}