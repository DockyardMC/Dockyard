package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerSelectedHotbarSlotChangeEvent
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.player.GameMode
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Set Held Item (serverbound)")
@ServerboundPacketInfo(47, ProtocolState.PLAY)
class ServerboundSetPlayerHeldItemPacket(val slot: Int): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        // Spectator mode scroll for fly speed
        val beforeSlot = processor.player.selectedHotbarSlot.value
        if(processor.player.gameMode.value == GameMode.SPECTATOR) {
            if(slot == 4) return
            val value = if(slot > 4) -0.1f else 0.1f
            processor.player.selectedHotbarSlot.value = 4
            processor.player.flySpeed.value = (processor.player.flySpeed.value + value).coerceIn(0.05f, 0.3f)
            DockyardServer.broadcastMessage("<gray>Fly speed now<yellow>${processor.player.flySpeed.value}")
            return
        }
        processor.player.selectedHotbarSlot.value = slot

        val event = PlayerSelectedHotbarSlotChangeEvent(processor.player, slot)
        Events.dispatch(event)

        if(event.cancelled) {
            processor.player.selectedHotbarSlot.value = beforeSlot
            return
        }
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundSetPlayerHeldItemPacket {
            return ServerboundSetPlayerHeldItemPacket(buf.readShort().toInt())
        }
    }
}