package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerChatMessageEvent
import io.github.dockyardmc.extentions.readFixedBitSet
import io.github.dockyardmc.extentions.readInstant
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.protocol.readOptional
import io.github.dockyardmc.utils.getPlayerEventContext
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import kotlinx.datetime.Instant
import java.util.*

data class ServerboundPlayerChatMessagePacket(var message: String, val timestamp: Instant, val salt: Long, val signature: ByteBuf?, val ackOffset: Int, val ackList: BitSet? = null, val checksum: Byte) : ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        val event = PlayerChatMessageEvent(message, processor.player, getPlayerEventContext(processor.player))
        Events.dispatch(event)
        if (event.cancelled) return

        DockyardServer.sendMessage("<white>${event.player}: <white>${event.message}")
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundPlayerChatMessagePacket {
            val text = buf.readString()
            val timestamp = buf.readInstant()
            val salt = buf.readLong()
            val signature = buf.readOptional {
                val signatureBuffer = buf.readBytes(256)
                val signature = signatureBuffer.retainedDuplicate()
                signatureBuffer.release()
                signature.release()
                signature
            }
            val ackOffset = buf.readVarInt()
            val ackList = buf.readFixedBitSet(20)
            val checksum = buf.readByte()

            return ServerboundPlayerChatMessagePacket(text, timestamp, salt, signature, ackOffset, ackList, checksum)
        }
    }
}