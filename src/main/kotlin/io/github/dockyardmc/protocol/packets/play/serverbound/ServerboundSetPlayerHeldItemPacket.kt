package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerSelectedHotbarSlotChangeEvent
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Set Held Item (serverbound)")
@ServerboundPacketInfo(47, ProtocolState.PLAY)
class ServerboundSetPlayerHeldItemPacket(val slot: Int): ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        // Spectator mode scroll for fly speed
        val beforeSlot = processor.player.heldSlot.value
        processor.player.heldSlot.value = slot

        val event = PlayerSelectedHotbarSlotChangeEvent(processor.player, slot)
        Events.dispatch(event)

        if(event.cancelled) {
            processor.player.heldSlot.value = beforeSlot
            return
        }
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundSetPlayerHeldItemPacket =
            ServerboundSetPlayerHeldItemPacket(buf.readShort().toInt())
    }
}