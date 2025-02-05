package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerLoadedEvent
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.utils.getPlayerEventContext
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundPlayerLoadedPacket: ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        Events.dispatch(PlayerLoadedEvent(processor.player, getPlayerEventContext(processor.player)))
    }

    companion object {
        fun read(buffer: ByteBuf): ServerboundPlayerLoadedPacket {
            return ServerboundPlayerLoadedPacket()
        }
    }
}