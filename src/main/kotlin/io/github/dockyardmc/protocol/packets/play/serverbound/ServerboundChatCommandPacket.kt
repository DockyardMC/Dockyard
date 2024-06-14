package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.commands.CommandExecutor
import io.github.dockyardmc.commands.CommandHandler
import io.github.dockyardmc.extentions.readFixedBitSet
import io.github.dockyardmc.extentions.readInstant
import io.github.dockyardmc.extentions.readUtf
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.utils.Console
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Chat Command")
@ServerboundPacketInfo(4, ProtocolState.PLAY)
class ServerboundChatCommandPacket(val command: String): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        CommandHandler.handleCommand(command, CommandExecutor(player = processor.player, console = Console))
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundChatCommandPacket {

            val command = buf.readUtf(256)
            val timestamp = buf.readInstant()
            val salt = buf.readLong()

            val arrLength = buf.readVarInt()
            repeat(arrLength) {
                val argumentName = buf.readUtf()
                val argumentSignature = buf.readBytes(256)
            }
            val messageCount = buf.readVarInt()
            val ack = buf.readFixedBitSet(20)

            return ServerboundChatCommandPacket(command)
        }
    }
}