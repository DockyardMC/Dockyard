package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundSetCreativeModeSlotPacket(var slot: Int, var clickedItem: SlotData): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        DockyardServer.broadcastMessage("<yellow>${processor.player}<gray> clicked slot <lime>$slot<gray> with <aqua>$clickedItem")
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundSetCreativeModeSlotPacket {
            val packet = ServerboundSetCreativeModeSlotPacket(buf.readShort().toInt(), buf.readSlotData());
            buf.clear()
            return packet
        }
    }
}