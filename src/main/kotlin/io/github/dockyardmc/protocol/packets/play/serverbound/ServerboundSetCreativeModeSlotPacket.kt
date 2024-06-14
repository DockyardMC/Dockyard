package io.github.dockyardmc.protocol.packets.play.serverbound

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.inventory.ItemStack
import io.github.dockyardmc.inventory.TempItemStack
import io.github.dockyardmc.inventory.readItemStack
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.utils.MathUtils
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Set Creative Mode Slot")
@ServerboundPacketInfo(50, ProtocolState.PLAY)
class ServerboundSetCreativeModeSlotPacket(var slot: Int, var clickedItem: TempItemStack): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {

        val player = processor.player
        val correctSlot = MathUtils.toCorrectSlotIndex(slot)
        // item removed if present is not set
        if(!clickedItem.present) {
            player.inventory.set(correctSlot, ItemStack.air)
            return
        }
        
        player.inventory.set(correctSlot, ItemStack(Items.getItemById(clickedItem.itemId!!), clickedItem.itemCount!!))

        DockyardServer.broadcastMessage("<yellow>$player<gray> clicked slot <lime>$correctSlot<gray> with <aqua>${clickedItem.itemId}")
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundSetCreativeModeSlotPacket {
            log("reading buf in packet.read(): buf ref count ${buf.refCnt()}", LogType.TRACE)

            val slot = buf.readShort().toInt()
            log("slot read: buf ref count ${buf.refCnt()}", LogType.TRACE)

            val clickedItem = buf.readItemStack()
            log("slot data read: buf ref count ${buf.refCnt()}", LogType.TRACE)

            return ServerboundSetCreativeModeSlotPacket(slot, clickedItem)
        }
    }
}