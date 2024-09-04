package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.commands.CommandExecutor
import io.github.dockyardmc.commands.CommandHandler
import io.github.dockyardmc.extentions.readString
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
        CommandHandler.handleCommandInput(command, CommandExecutor(player = processor.player, console = Console))
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundChatCommandPacket {

            val command = buf.readString(32767)

            return ServerboundChatCommandPacket(command)
        }
    }
}