package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Command Suggestions Request")
@ServerboundPacketInfo(0x0B, ProtocolState.PLAY)
class ServerboundCommandSuggestionPacket(var transactionId: Int, var text: String): ServerboundPacket {
    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        DockyardServer.broadcastMessage("$transactionId $text")
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundCommandSuggestionPacket =
            ServerboundCommandSuggestionPacket(buf.readVarInt(), buf.readString())
    }
}