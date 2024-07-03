package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.extentions.readVarIntEnum
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@ServerboundPacketInfo(0x09, ProtocolState.PLAY)
class ServerboundClientStatusPacket(val action: ClientStatusAction): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        if(action == ClientStatusAction.RESPAWN) {
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