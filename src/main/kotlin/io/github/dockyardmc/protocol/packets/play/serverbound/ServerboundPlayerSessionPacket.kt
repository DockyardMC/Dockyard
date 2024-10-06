package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.readByteArray
import io.github.dockyardmc.extentions.readInstant
import io.github.dockyardmc.extentions.readUUID
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.cryptography.PlayerSession
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Player Session")
@ServerboundPacketInfo(7, ProtocolState.PLAY)
class ServerboundPlayerSessionPacket(val playerSession: PlayerSession): ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.player.crypto.playerSession = playerSession
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundPlayerSessionPacket {
            return ServerboundPlayerSessionPacket(PlayerSession(
                buf.readUUID(),
                buf.readInstant(),
                buf.readByteArray(),
                buf.readByteArray()
            ))
        }
    }

}