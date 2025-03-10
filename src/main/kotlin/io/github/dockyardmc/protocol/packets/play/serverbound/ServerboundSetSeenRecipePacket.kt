package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundSetSeenRecipePacket(identifier: String) : ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {

    }

    companion object {
        fun read(byteBuf: ByteBuf): ServerboundSetSeenRecipePacket = ServerboundSetSeenRecipePacket(byteBuf.readString())
    }
}