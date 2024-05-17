package io.github.dockyardmc.protocol.packets.play.serverbound

import LogType
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerFlightToggleEvent
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import log

class ServerboundPlayerAbilitiesPacket(val flying: Boolean): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        log("Player ${processor.player} fly state is now $flying", LogType.DEBUG)
        processor.player.isFlying = flying
        Events.dispatch(PlayerFlightToggleEvent(processor.player, flying))
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundPlayerAbilitiesPacket {
            val byte = buf.readByte()
            val flying = byte.toInt() == 2
            return ServerboundPlayerAbilitiesPacket(flying)
        }
    }
}