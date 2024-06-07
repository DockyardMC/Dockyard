package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.commands.CommandExecutor
import io.github.dockyardmc.commands.CommandHandler
import io.github.dockyardmc.extentions.readFixedBitSet
import io.github.dockyardmc.extentions.readInstant
import io.github.dockyardmc.extentions.readUtf
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundChatCommandPacket(val command: String): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        CommandHandler.handleCommand(command, CommandExecutor(player = processor.player))
//        DockyardServer.broadcastMessage("<yellow>${processor.player} executed <lime>/$command")
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