package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.extentions.readVarIntEnum
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundClientStatusPacket(val action: ClientStatusAction) : ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        if (action == ClientStatusAction.RESPAWN) {
            processor.player.respawn(true)
        }
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundClientStatusPacket = ServerboundClientStatusPacket(buf.readVarIntEnum<ClientStatusAction>())
    }
}

enum class ClientStatusAction {
    RESPAWN,
    REQUEST_STATS
}