package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Close Container")
@ServerboundPacketInfo(18, ProtocolState.PLAY)
class ServerboundContainerClosePacket(val windowId: Int): ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        DockyardServer.broadcastMessage("<red>${processor.player} closed inventory id <yellow>$windowId")
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundContainerClosePacket {
            return ServerboundContainerClosePacket(buf.readByte().toInt())
        }
    }
}