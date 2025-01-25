package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Set Player Rotation")
@ServerboundPacketInfo(28, ProtocolState.PLAY)
class ServerboundSetPlayerRotationPacket(var yaw: Float, var pitch: Float, var isOnGround: Boolean): ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.playHandler.handlePlayerPositionAndRotationUpdates(this, connection)
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundSetPlayerRotationPacket {
            return ServerboundSetPlayerRotationPacket(buf.readFloat(), buf.readFloat(), buf.readBoolean())
        }
    }

}