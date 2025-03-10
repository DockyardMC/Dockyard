package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerChatMessageEvent
import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundPlayerChatMessagePacket(var message: String): ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        val event = PlayerChatMessageEvent(message, processor.player)
        Events.dispatch(event)
        if (event.cancelled) return

        DockyardServer.broadcastMessage("<white>${event.player}: <white>${event.message}")
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundPlayerChatMessagePacket {
            val text = buf.readString(256)
            val timestamp = buf.readInstant()
            val salt = buf.readLong()
            val hasSignature = buf.readBoolean()
            if(hasSignature) {
                val signatureBuffer = buf.readBytes(256)
                val signature = signatureBuffer.retainedDuplicate()
                signatureBuffer.release()
                signature.release()
            }
            val msgCount = buf.readVarInt()
            val ack = buf.readFixedBitSet(20)

            return ServerboundPlayerChatMessagePacket(text)
        }
    }
}