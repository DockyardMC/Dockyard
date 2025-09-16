package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerSelectedHotbarSlotChangeEvent
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.utils.getPlayerEventContext
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundSetPlayerHeldItemPacket(val slot: Int) : ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        val player = processor.player
        val beforeSlot = player.heldSlotIndex.value

        val event = PlayerSelectedHotbarSlotChangeEvent(processor.player, slot, getPlayerEventContext(player))
        Events.dispatch(event)

        if (event.cancelled) {
            player.heldSlotIndex.value = beforeSlot
            return
        }
        player.heldSlotIndex.value = slot
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundSetPlayerHeldItemPacket =
            ServerboundSetPlayerHeldItemPacket(buf.readShort().toInt())
    }
}