package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.inventory.ItemStack
import io.github.dockyardmc.inventory.readItemStack
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.utils.MathUtils
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Set Creative Mode Slot")
@ServerboundPacketInfo(50, ProtocolState.PLAY)
class ServerboundSetCreativeModeSlotPacket(var slot: Int, var clickedItem: ItemStack): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {

        val player = processor.player
        val correctSlot = MathUtils.toCorrectSlotIndex(slot)

        player.inventory.set(correctSlot, clickedItem)

        DockyardServer.broadcastMessage("<yellow>$player<gray> clicked slot <lime>$correctSlot<gray> with <aqua>${clickedItem}")
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundSetCreativeModeSlotPacket {
            val slot = buf.readShort().toInt()
            val clickedItem = buf.readItemStack()
            return ServerboundSetCreativeModeSlotPacket(slot, clickedItem)
        }
    }
}