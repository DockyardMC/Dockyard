package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerSelectedHotbarSlotChangeEvent
import io.github.dockyardmc.player.GameMode
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundSetPlayerHeldItemPacket(val slot: Int): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        if(processor.player.gameMode == GameMode.SPECTATOR) {
            if(slot == 4) return
            val value = if(slot > 4) -0.1f else 0.1f
            processor.player.setSelHotbarSlot(4)
            processor.player.flySpeed.value = (processor.player.flySpeed.value + value).coerceIn(0.05f, 0.3f)
            DockyardServer.broadcastMessage("<gray>Fly speed now<yellow>${processor.player.flySpeed.value}")
            return
        }

        Events.dispatch(PlayerSelectedHotbarSlotChangeEvent(processor.player, slot))
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundSetPlayerHeldItemPacket {
            return ServerboundSetPlayerHeldItemPacket(buf.readShort().toInt())
        }
    }
}