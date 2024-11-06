package io.github.dockyardmc.protocol.packets.play.serverbound

import cz.lukynka.prettylog.log
import io.github.dockyardmc.inventory.PlayerInventoryUtils
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.item.readItemStack
import io.github.dockyardmc.player.GameMode
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundSetCreativeModeSlotPacket(var slot: Int, var clickedItem: ItemStack) : ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        val player = processor.player
        if(player.gameMode.value != GameMode.CREATIVE) return
        if(slot == -1) {
            //drop
            player.inventory.drop(clickedItem)
            return
        }

        if(slot < 1 || slot > PlayerInventoryUtils.OFFHAND_SLOT) {
            return
        }

        val newSlot = PlayerInventoryUtils.convertPlayerInventorySlot(slot, PlayerInventoryUtils.OFFSET)
        if(player.inventory[newSlot] == clickedItem) return

        player.inventory[newSlot] = clickedItem
        log("Set creative mode slot $newSlot to $clickedItem")
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundSetCreativeModeSlotPacket {
            val slot = buf.readShort().toInt()
            val clickedItem = buf.readItemStack()
            return ServerboundSetCreativeModeSlotPacket(slot, clickedItem)
        }
    }
}