package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerClientSettingsEvent
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.protocol.types.ClientSettings
import io.github.dockyardmc.tide.Codec
import io.github.dockyardmc.utils.getPlayerEventContext
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

data class ServerboundClientSettingsUpdatePacket(val clientSettings: ClientSettings) : ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        val event = PlayerClientSettingsEvent(clientSettings, processor.player, getPlayerEventContext(processor.player))
        Events.dispatch(event)

        processor.player.clientSettings = clientSettings
    }

    companion object : NetworkReadable<ServerboundClientSettingsUpdatePacket> {

        val STREAM_CODEC = Codec.of(
            "client_settings", ClientSettings.STREAM_CODEC, ServerboundClientSettingsUpdatePacket::clientSettings,
            ::ServerboundClientSettingsUpdatePacket
        )

        override fun read(buffer: ByteBuf): ServerboundClientSettingsUpdatePacket {
            return STREAM_CODEC.readNetwork(buffer)
        }
    }
}