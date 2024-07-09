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


        val clickedSlotItem = player.inventory[properSlot].clone()
        val empty = ItemStack.air

        if(mode == ContainerClickMode.NORMAL) {
            val action = NormalButtonAction.entries.find { it.button == button }
            if(action == null) {
                player.sendMessage("<red>action is null!")
                return
            }
            player.sendMessage("<yellow>${action.name}")

            if(action == NormalButtonAction.LEFT_MOUSE_CLICK) {

                // drop
                if(slot == -999) {
                    player.inventory.drop(player.inventory.carriedItem)
                    player.inventory.carriedItem = empty
                    player.sendMessage("<red>drop")
                    return
                }

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
                            player.sendMessage("<yellow>combined")
                            return
                        }
                        // Swap the items if they are not the same item stack
                        val before = player.inventory[properSlot].clone()
                        player.inventory[properSlot] = player.inventory.carriedItem
                        player.inventory.carriedItem = before
                        player.sendMessage("<orange>swap")
                        return
                    }
                }
            }

            if(action == NormalButtonAction.RIGHT_MOUSE_CLICK) {

                if(slot == -999) {
                    player.inventory.drop(player.inventory.carriedItem.clone().apply { amount = 1 })
                    val item = player.inventory.carriedItem.clone().apply { amount -= 1 }
                    val newItem = if(item.amount == 0) empty else item
                    player.inventory.carriedItem = newItem
                    player.sendMessage("<red>drop 1")
                    return
                }

                if(clickedSlotItem.isSameAs(empty)) {

                    // put 1 to new slot
                    if(!player.inventory.carriedItem.isSameAs(empty)) {
                        player.inventory[properSlot] = player.inventory.carriedItem.clone().apply { amount = 1 }
                        val newCarried = player.inventory.carriedItem.clone().apply { amount -= 1 }
                        val newItem = if(newCarried.amount == 0) empty else newCarried
                        player.inventory.carriedItem = newItem
                        player.sendMessage("<lime>put")
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
                    } else {
                        // make sure its item not nothing
                        if(!player.inventory.carriedItem.isSameAs(empty)) {
                            val before = player.inventory[properSlot].clone()

                            player.inventory[properSlot] = player.inventory.carriedItem
                            player.inventory.carriedItem = before
                            player.sendMessage("<orange>swap")
                        }
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
        if(mode == ContainerClickMode.NORMAL_SHIFT) {
            val action = NormalShiftButtonAction.entries.find { it.button == button }
            if(action == null) {
                player.sendMessage("<red>action is null!")
                return
            }

            if(action == NormalShiftButtonAction.SHIFT_LEFT_MOUSE_CLICK || action == NormalShiftButtonAction.SHIFT_RIGHT_MOUSE_CLICK) {

                // Move from hotbar if more than 9, else move to hotbar
                val suitableSlotIndex = if (properSlot <= 9) {
                    (27..35).firstOrNull { player.inventory[it] == empty || player.inventory[it].isSameAs(clickedSlotItem) } ?: (0..26).firstOrNull { player.inventory[it] == empty || player.inventory[it].isSameAs(clickedSlotItem) }
                } else {
                    (0..8).firstOrNull { player.inventory[it] == empty || player.inventory[it].isSameAs(clickedSlotItem) }
                }

                if(suitableSlotIndex == null) return
                val existingItem = player.inventory[suitableSlotIndex].clone()

                if(existingItem.isSameAs(empty)) return

                if(!existingItem.isSameAs(empty) && existingItem.isSameAs(clickedSlotItem)) {
                    val totalAmount = existingItem.amount + clickedSlotItem.amount
                    if(totalAmount <= existingItem.maxStackSize.value) {
                        player.inventory[properSlot] = empty
                        player.inventory[suitableSlotIndex] = existingItem.clone().apply { amount = totalAmount }

                    } else {
                        player.inventory[properSlot] = clickedSlotItem.clone().apply { clickedSlotItem.amount = (totalAmount - existingItem.maxStackSize.value) }
                        player.inventory[suitableSlotIndex] = existingItem.clone().apply { existingItem.amount = existingItem.maxStackSize.value }
                    }
                } else {
                    player.inventory[properSlot] = empty
                    player.inventory[suitableSlotIndex] = clickedSlotItem.clone()
                }
                // cause fuck you idk desync bugs
                player.inventory.sendFullInventoryUpdate()
            }
        }
        if(mode == ContainerClickMode.HOTKEY) {
            val action = if(button == 40) HotkeyButtonAction.OFFHAND_SWAP else HotkeyButtonAction.CHANGE_TO_SLOT

            if(action == HotkeyButtonAction.CHANGE_TO_SLOT) {
                val existingItem = player.inventory[properSlot].clone()
                val swappedItem = player.inventory[button].clone()

                player.inventory[properSlot] = swappedItem
                player.inventory[button] = existingItem
            }
            if(action == HotkeyButtonAction.OFFHAND_SWAP) {
                val existingItem = player.inventory[properSlot].clone()
                val swappedItem = player.inventory[40].clone()

                player.inventory[properSlot] = swappedItem
                player.inventory[40] = existingItem
            }
        }
        if(mode == ContainerClickMode.DROP) {
            val action = DropButtonAction.entries.find { it.button == button }
            if(action == null) {
                player.sendMessage("<red>action is null!")
                return
            }

            if(action == DropButtonAction.DROP) {
                val existingItem = player.inventory[properSlot].clone()
                if(existingItem.isSameAs(empty)) return

                val newItem = if(existingItem.amount == 1) empty else existingItem.clone().apply { amount -= 1 }
                player.inventory.drop(existingItem.apply { amount = 1 })
                player.inventory[properSlot] = newItem
            }
            if(action == DropButtonAction.CONTROL_DROP) {
                val existingItem = player.inventory[properSlot].clone()
                if(existingItem.isSameAs(empty)) return

                player.inventory.drop(existingItem)
                player.inventory[properSlot] = empty
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

enum class NormalShiftButtonAction(val button: Int, val outsideInv: Boolean = false) {
    SHIFT_LEFT_MOUSE_CLICK(0),
    SHIFT_RIGHT_MOUSE_CLICK(1),
}

enum class HotkeyButtonAction(val button: Int) {
    CHANGE_TO_SLOT(0),
    OFFHAND_SWAP(40)
}

enum class DropButtonAction(val button: Int, val outsideInv: Boolean = false) {
    DROP(0),
    CONTROL_DROP(1)
}

enum class DoubleClickButtonAction(button: Int, outsideInv: Boolean = false) {
    DOUBLE_CLICK(0),
    PICKUP_ALL(1)
}