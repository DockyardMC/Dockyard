package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerFlightToggleEvent
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.utils.getPlayerEventContext
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundPlayerAbilitiesPacket(val flying: Boolean) : ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        val player = processor.player
        processor.player.isFlying.setSilently(flying)
        val event = PlayerFlightToggleEvent(player, flying, getPlayerEventContext(player))
        Events.dispatch(event)
        if (event.cancelled) {
            player.isFlying.value = false
        }
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundPlayerAbilitiesPacket {
            val byte = buf.readByte()
            val flying = byte.toInt() == 2
            return ServerboundPlayerAbilitiesPacket(flying)
        }
    }
}