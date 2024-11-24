package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.inventory.PlayerInventoryUtils
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.item.readItemStack
import io.github.dockyardmc.player.systems.GameMode
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
            val cancelled = player.inventory.drop(clickedItem)
            if(cancelled) {
                player.inventory.sendFullInventoryUpdate()
                player.inventory.cursorItem.value = clickedItem
                return
            }
            return
        }

        if(slot < 1 || slot > PlayerInventoryUtils.OFFHAND_SLOT) {
            return
        }

        val newSlot = PlayerInventoryUtils.convertPlayerInventorySlot(slot, PlayerInventoryUtils.OFFSET)
        if(player.inventory[newSlot] == clickedItem) return

        val equipmentSlot = player.inventory.getEquipmentSlot(newSlot, player.heldSlotIndex.value)
        if(equipmentSlot != null) {
            player.equipment[equipmentSlot] = clickedItem
            player.inventory.cursorItem.value = ItemStack.AIR
            return
        }


        player.inventory[newSlot] = clickedItem
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundSetCreativeModeSlotPacket {
            val slot = buf.readShort().toInt()
            val clickedItem = buf.readItemStack()
            return ServerboundSetCreativeModeSlotPacket(slot, clickedItem)
        }
    }
}