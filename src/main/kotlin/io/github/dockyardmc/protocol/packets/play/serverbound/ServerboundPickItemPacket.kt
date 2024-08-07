package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Pick Item")
@ServerboundPacketInfo(0x20, ProtocolState.PLAY)
class ServerboundPickItemPacket(slot: Int): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        val player = processor.player
        player.sendMessage("<pink>picked block")
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundPickItemPacket = ServerboundPickItemPacket(buf.readVarInt())
    }
}