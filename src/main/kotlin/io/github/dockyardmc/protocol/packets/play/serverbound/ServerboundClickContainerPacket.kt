package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.extentions.readVarIntEnum
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.item.clone
import io.github.dockyardmc.item.isSameAs
import io.github.dockyardmc.item.readItemStack
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.utils.MathUtils
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Click Container")
@ServerboundPacketInfo(14, ProtocolState.PLAY)
class ServerboundClickContainerPacket(
    var windowId: Int,
    var stateId: Int,
    var slot: Int,
    var button: Int,
    var mode: ContainerClickMode,
    var changedSlots: MutableMap<Int, ItemStack>,
    var item: ItemStack
): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        val player = processor.player
        val properSlot = MathUtils.toCorrectSlotIndex(slot)
        DockyardServer.broadcastMessage("<dark_gray>${item.material.identifier} $properSlot ($slot) [${mode.name}]")

        // If windowId is 0 that means it's the players inventory
        if(mode == ContainerClickMode.NORMAL) {
            val action = NormalButtonAction.entries.find { it.button == button }
            if(action == null) {
                player.sendMessage("<red>action is null!")
                return
            }

            player.sendMessage("<yellow>${action.name}")
            val clickedSlotItem = player.inventory[properSlot].clone()
            val empty = ItemStack.air

            if(action == NormalButtonAction.LEFT_MOUSE_CLICK) {

                if(clickedSlotItem.isSameAs(empty) && player.inventory.carriedItem.isSameAs(empty)) {
                    player.inventory[properSlot] = empty
                    return
                }

                // Set carried item to what player clicked
                if(clickedSlotItem.isSameAs(empty)) {
                    if (player.inventory.carriedItem != empty) {
                        player.inventory[properSlot] = player.inventory.carriedItem
                        player.inventory.carriedItem = empty
                        return
                    }

                } else {

                    // Set carried slot to what player clicks on item with no carried
                    if(player.inventory.carriedItem.isSameAs(empty)) {
                        val before = player.inventory[properSlot].clone()
                        player.inventory.carriedItem = before
                        player.inventory[properSlot] = empty
                        return
                    }

                    if(!player.inventory.carriedItem.isSameAs(empty)) {
                        // Combine items if they are the same item stack
                        if(player.inventory.carriedItem.isSameAs(clickedSlotItem)) {
                            player.inventory[properSlot] = player.inventory[properSlot].clone().apply { amount += player.inventory.carriedItem.amount }
                            player.inventory.carriedItem = empty
                            player.sendMessage("<orange>combined")
                            return
                        }
                        // Swap the items if they are not the same item stack
                        val before = player.inventory[properSlot].clone()
                        player.inventory[properSlot] = player.inventory.carriedItem
                        player.inventory.carriedItem = before
                        player.sendMessage("<lime>swapped")
                        return
                    }
                }
            }

            if(action == NormalButtonAction.LEFT_CLICK_OUTSIDE_INVENTORY) {
                //TODO drop
            }

            if(action == NormalButtonAction.RIGHT_CLICK_OUTSIDE_INVENTORY) {
                //TODO drop one
            }

            if(action == NormalButtonAction.RIGHT_MOUSE_CLICK) {
                if(clickedSlotItem.isSameAs(empty)) {

                    // put 1 to new slot
                    if(!player.inventory.carriedItem.isSameAs(empty)) {
                        player.inventory[properSlot] = player.inventory.carriedItem.clone().apply { amount = 1 }
                        val newCarried = player.inventory.carriedItem.clone().apply { amount -= 1 }
                        val newItem = if(newCarried.amount == 0) empty else newCarried
                        player.inventory.carriedItem = newItem
                        return
                    }

                } else {

                    // combine the current +1
                    if(player.inventory.carriedItem.isSameAs(clickedSlotItem)) {
                        player.inventory[properSlot] = clickedSlotItem.clone().apply { amount += 1 }
                        val newCarried = player.inventory.carriedItem.clone().apply { amount -= 1 }
                        val newItem = if(newCarried.amount == 0) empty else newCarried
                        player.inventory.carriedItem = newItem
                        return
                    }

                    if(player.inventory.carriedItem.isSameAs(empty)) {
                        val before = player.inventory[properSlot].clone()

                        var half = before.amount.div(2)
                        if(half == 0) half = 1

                        val newSlotAmount = before.amount - half

                        val newSlotItem = if(newSlotAmount == 0) empty else before.clone().apply { amount = newSlotAmount }

                        player.inventory[properSlot] = newSlotItem
                        player.inventory.carriedItem = before.clone().apply { amount = half }

                        player.sendMessage("<aqua>take half")
                        return
                    }
                }
            }
        }
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundClickContainerPacket {
            val windowsId = buf.readByte().toInt()
            val stateId = buf.readVarInt()
            val slot = buf.readShort().toInt()
            val button = buf.readByte().toInt()
            val mode = buf.readVarIntEnum<ContainerClickMode>()
            val changedSlots = mutableMapOf<Int, ItemStack>()

            val arraySize = buf.readVarInt()
            repeat(arraySize) {
                val slotNumber = buf.readShort().toInt()
                val slotData = buf.readItemStack()
                changedSlots[slotNumber] = slotData
            }

            val carriedItem = buf.readItemStack()




            val rest = buf.readableBytes()
            buf.readBytes(rest)
            buf.clear()

            return ServerboundClickContainerPacket(windowsId, stateId, slot, button, mode, changedSlots, carriedItem)
        }
    }
}

enum class ContainerClickMode {
    NORMAL,
    NORMAL_SHIFT,
    HOTKEY,
    MIDDLE_CLICK,
    DROP,
    SLOT_DRAG,
    DOUBLE_CLICK
}

enum class NormalButtonAction(val button: Int, val outsideInv: Boolean = false) {
    LEFT_MOUSE_CLICK(0),
    RIGHT_MOUSE_CLICK(1),
    LEFT_CLICK_OUTSIDE_INVENTORY(0, true),
    RIGHT_CLICK_OUTSIDE_INVENTORY(1, true),
}

enum class DragButtonAction(button: Int, outsideInv: Boolean = false) {
    STARTING_LEFT_MOUSE_DRAG(0, true),
    STARTING_RIGHT_MOUSE_DRAG(4, true),
    STARTING_MIDDLE_MOUSE_DRAG(8, true),
    ADD_SLOT_FOR_LEFT_MOUSE_DRAG(1),
    ADD_SLOT_FOR_RIGHT_MOUSE_DRAG(5),
    ADD_SLOT_FOR_MIDDLE_MOUSE_DRAG(9),
    ENDING_LEFT_MOUSE_DRAG(2, true),
    ENDING_RIGHT_MOUSE_DRAG(6, true),
    ENDING_MIDDLE_MOUSE_DRAG(10, true)
}

enum class NormalShiftButtonAction(button: Int, outsideInv: Boolean = false) {
    SHIFT_LEFT_MOUSE_CLICK(0),
    SHIFT_RIGHT_MOUSE_CLICK(1),
}

enum class HotkeyButtonAction {
    CHANGE_TO_SLOT,
    OFFHAND_SWAP
}

enum class DropButtonAction(button: Int, outsideInv: Boolean = false) {
    DROP(0),
    CONTROL_DROP(1)
}

enum class DoubleClickButtonAction(button: Int, outsideInv: Boolean = false) {
    DOUBLE_CLICK(0),
    PICKUP_ALL(1)
}