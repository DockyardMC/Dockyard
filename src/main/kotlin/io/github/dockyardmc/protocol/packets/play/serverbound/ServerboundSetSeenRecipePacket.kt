package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Set Seen Recipe")
@ServerboundPacketInfo(0x29, ProtocolState.PLAY)
class ServerboundSetSeenRecipePacket(identifier: String): ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {

    }

    companion object {
        fun read(byteBuf: ByteBuf): ServerboundSetSeenRecipePacket = ServerboundSetSeenRecipePacket(byteBuf.readString())
    }
}