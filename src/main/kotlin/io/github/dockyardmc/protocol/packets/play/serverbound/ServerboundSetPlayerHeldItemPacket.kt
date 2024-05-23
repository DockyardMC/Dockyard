package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerSelectedHotbarSlotChangeEvent
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundSetPlayerHeldItemPacket(val slot: Int): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.player.selectedHotbarSlot = slot
        Events.dispatch(PlayerSelectedHotbarSlotChangeEvent(processor.player, slot))
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundSetPlayerHeldItemPacket {
            return ServerboundSetPlayerHeldItemPacket(buf.readShort().toInt())
        }
    }
}