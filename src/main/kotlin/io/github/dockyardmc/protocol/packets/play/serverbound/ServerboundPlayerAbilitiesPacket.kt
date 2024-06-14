package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerFlightToggleEvent
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Player Abilities (serverbound)")
@ServerboundPacketInfo(32, ProtocolState.PLAY)
class ServerboundPlayerAbilitiesPacket(val flying: Boolean): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.player.isFlying.setSilently(flying)
        val event = PlayerFlightToggleEvent(processor.player, flying)
        Events.dispatch(event)
        if(event.cancelled) {
            processor.player.isFlying.value = false
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