package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.readEnum
import io.github.dockyardmc.player.ClientParticleSettings
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundClientInformationPacket(
    var locale: String,
    var viewDistance: Int,
    var chatMode: Int,
    var chatColors: Boolean,
    var displayedSkinParts: Byte,
    var mainHandSide: PlayerHand,
    var enableTextFiltering: Boolean,
    var allowServerListing: Boolean,
    var particleSettings: ClientParticleSettings
): ServerboundPacket {
    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.configurationHandler.handleClientInformation(this, connection)
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundClientInformationPacket {
            return ServerboundClientInformationPacket(
                buf.readString(),
                buf.readByte().toInt(),
                buf.readVarInt(),
                buf.readBoolean(),
                buf.readByte(),
                buf.readEnum<PlayerHand>(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readEnum<ClientParticleSettings>()
            )
        }
    }
}