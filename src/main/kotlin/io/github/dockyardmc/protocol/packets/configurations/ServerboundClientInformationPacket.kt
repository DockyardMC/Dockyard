package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Client Information (configuration)")
@ServerboundPacketInfo(0x00, ProtocolState.CONFIGURATION)
class ServerboundClientInformationPacket(
    var locale: String,
    var viewDistance: Int,
    var chatMode: Int,
    var chatColors: Boolean,
    var displayedSkinParts: Byte,
    var mainHandSide: Int,
    var enableTextFiltering: Boolean,
    var allowServerListing: Boolean
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
                buf.readVarInt(),
                buf.readBoolean(),
                buf.readBoolean()
            )
        }
    }
}