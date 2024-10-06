package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.readVarIntEnum
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.item.clone
import io.github.dockyardmc.item.isSameAs
import io.github.dockyardmc.item.readItemStack
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.ui.DrawableClickType
import io.github.dockyardmc.ui.DrawableContainerScreen
import io.github.dockyardmc.utils.playerInventoryCorrectSlot
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import java.lang.IllegalStateException

@WikiVGEntry("Click Container")
@ServerboundPacketInfo(0x0E, ProtocolState.PLAY)
class ServerboundClickContainerPacket(
    var windowId: Int,
    var stateId: Int,
    var slot: Int,
    var button: Int,
    var mode: ContainerClickMode,
    var changedSlots: MutableMap<Int, ItemStack>,
    var item: ItemStack,
): ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        val player = processor.player
        val currentInventory = player.currentOpenInventory
        val properSlot = if(player.currentOpenInventory == null) playerInventoryCorrectSlot(slot) else slot

        val clickedSlotItem = player.inventory[properSlot].clone()
        val empty = ItemStack.air

        var drawableClickType = DrawableClickType.LEFT_CLICK
        when(mode) {
            ContainerClickMode.NORMAL -> {
                val action = NormalButtonAction.entries.find { it.button == button } ?: return
                drawableClickType = when(action) {
                    NormalButtonAction.LEFT_MOUSE_CLICK -> DrawableClickType.LEFT_CLICK
                    NormalButtonAction.RIGHT_MOUSE_CLICK -> DrawableClickType.RIGHT_CLICK
                    NormalButtonAction.LEFT_CLICK_OUTSIDE_INVENTORY -> DrawableClickType.LEFT_CLICK_OUTSIDE_INVENTORY
                    NormalButtonAction.RIGHT_CLICK_OUTSIDE_INVENTORY -> DrawableClickType.RIGHT_CLICK_OUTSIDE_INVENTORY
                }
            }
            ContainerClickMode.NORMAL_SHIFT -> {
                val action = NormalShiftButtonAction.entries.find { it.button == button } ?: return
                drawableClickType = when(action) {
                    NormalShiftButtonAction.SHIFT_LEFT_MOUSE_CLICK -> DrawableClickType.LEFT_CLICK_SHIFT
                    NormalShiftButtonAction.SHIFT_RIGHT_MOUSE_CLICK -> DrawableClickType.RIGHT_CLICK_SHIFT
                }
            }
            ContainerClickMode.HOTKEY -> {
                val action = if(button == 40) HotkeyButtonAction.OFFHAND_SWAP else HotkeyButtonAction.CHANGE_TO_SLOT
                drawableClickType = if(action == HotkeyButtonAction.OFFHAND_SWAP) DrawableClickType.OFFHAND else DrawableClickType.HOTKEY
            }
            ContainerClickMode.MIDDLE_CLICK -> drawableClickType = DrawableClickType.MIDDLE_CLICK
            ContainerClickMode.DROP -> drawableClickType = DrawableClickType.DROP
            ContainerClickMode.SLOT_DRAG -> {
                val action = DragButtonAction.entries.find { it.button == button }
                drawableClickType = when(action) {
                    DragButtonAction.STARTING_LEFT_MOUSE_DRAG,
                    DragButtonAction.ADD_SLOT_FOR_LEFT_MOUSE_DRAG,
                    DragButtonAction.ENDING_LEFT_MOUSE_DRAG -> DrawableClickType.LEFT_CLICK

                    DragButtonAction.STARTING_RIGHT_MOUSE_DRAG,
                    DragButtonAction.ENDING_RIGHT_MOUSE_DRAG,
                    DragButtonAction.ADD_SLOT_FOR_RIGHT_MOUSE_DRAG -> DrawableClickType.RIGHT_CLICK

                    DragButtonAction.STARTING_MIDDLE_MOUSE_DRAG,
                    DragButtonAction.ADD_SLOT_FOR_MIDDLE_MOUSE_DRAG,
                    DragButtonAction.ENDING_MIDDLE_MOUSE_DRAG -> DrawableClickType.MIDDLE_CLICK
                    null -> throw IllegalStateException("action with button $button of DragButtonAction not")
                }
            }
            ContainerClickMode.DOUBLE_CLICK -> drawableClickType = DrawableClickType.LEFT_CLICK
        }

        if(currentInventory != null && currentInventory is DrawableContainerScreen && properSlot >= 0) currentInventory.click(properSlot, player, drawableClickType)

        if(windowId == 0) {
            if(mode == ContainerClickMode.NORMAL) {
                val action = NormalButtonAction.entries.find { it.button == button }
                if(action == null) {
                    return
                }
                if(action == NormalButtonAction.LEFT_MOUSE_CLICK) {

                    // drop
                    if(slot == -999) {
                        player.inventory.drop(player.inventory.carriedItem.value, isEntireStack = true, isHeld = true)
                        player.inventory.carriedItem.value = empty
                        return
                    }

                    if(clickedSlotItem.isSameAs(empty) && player.inventory.carriedItem.value.isSameAs(empty)) {
                        player.inventory[properSlot] = empty
                        return
                    }

                    // Set carried item to what player clicked
                    if(clickedSlotItem.isSameAs(empty)) {
                        if (player.inventory.carriedItem.value != empty) {
                            player.inventory[properSlot] = player.inventory.carriedItem.value
                            player.inventory.carriedItem.value = empty
                            return
                        }

                    } else {

                        // Set carried slot to what player clicks on item with no carried
                        if(player.inventory.carriedItem.value.isSameAs(empty)) {
                            val before = player.inventory[properSlot].clone()
                            player.inventory.carriedItem.value = before
                            player.inventory[properSlot] = empty
                            return
                        }

                        if(!player.inventory.carriedItem.value.isSameAs(empty)) {
                            // Combine items if they are the same item stack
                            if(player.inventory.carriedItem.value.isSameAs(clickedSlotItem)) {
                                player.inventory[properSlot] = player.inventory[properSlot].clone().apply { amount += player.inventory.carriedItem.value.amount }
                                player.inventory.carriedItem.value = empty
                                return
                            }
                            // Swap the items if they are not the same item stack
                            val before = player.inventory[properSlot].clone()
                            player.inventory[properSlot] = player.inventory.carriedItem.value
                            player.inventory.carriedItem.value = before
                            return
                        }
                    }
                }

                if(action == NormalButtonAction.RIGHT_MOUSE_CLICK) {

                    if(slot == -999) {
                        player.inventory.drop(player.inventory.carriedItem.value.clone().apply { amount = 1 }, isEntireStack = false, isHeld = false)
                        val item = player.inventory.carriedItem.value.clone().apply { amount -= 1 }
                        val newItem = if(item.amount == 0) empty else item
                        player.inventory.carriedItem.value = newItem
                        return
                    }

                    if(clickedSlotItem.isSameAs(empty)) {

                        // put 1 to new slot
                        if(!player.inventory.carriedItem.value.isSameAs(empty)) {
                            player.inventory[properSlot] = player.inventory.carriedItem.value.clone().apply { amount = 1 }
                            val newCarried = player.inventory.carriedItem.value.clone().apply { amount -= 1 }
                            val newItem = if(newCarried.amount == 0) empty else newCarried
                            player.inventory.carriedItem.value = newItem
                            return
                        }

                    } else {

                        // combine the current +1
                        if(player.inventory.carriedItem.value.isSameAs(clickedSlotItem)) {
                            player.inventory[properSlot] = clickedSlotItem.clone().apply { amount += 1 }
                            val newCarried = player.inventory.carriedItem.value.clone().apply { amount -= 1 }
                            val newItem = if(newCarried.amount == 0) empty else newCarried
                            player.inventory.carriedItem.value = newItem
                            return
                        } else {
                            // make sure its item not nothing
                            if(!player.inventory.carriedItem.value.isSameAs(empty)) {
                                val before = player.inventory[properSlot].clone()

                                player.inventory[properSlot] = player.inventory.carriedItem.value
                                player.inventory.carriedItem.value = before
                            }
                        }

                        if(player.inventory.carriedItem.value.isSameAs(empty)) {
                            val before = player.inventory[properSlot].clone()

                            var half = before.amount.div(2)
                            if(half == 0) half = 1

                            val newSlotAmount = before.amount - half

                            val newSlotItem = if(newSlotAmount == 0) empty else before.clone().apply { amount = newSlotAmount }

                            player.inventory[properSlot] = newSlotItem
                            player.inventory.carriedItem.value = before.clone().apply { amount = half }

                            return
                        }
                    }
                }
            }
            if(mode == ContainerClickMode.NORMAL_SHIFT) {
                val action = NormalShiftButtonAction.entries.find { it.button == button }
                if(action == null) {
                    return
                }

                if(action == NormalShiftButtonAction.SHIFT_LEFT_MOUSE_CLICK || action == NormalShiftButtonAction.SHIFT_RIGHT_MOUSE_CLICK) {

                    // Move from hotbar if more than 9, else move to hotbar
                    val suitableSlotIndex = if (properSlot <= 9) {
                        (27..35).firstOrNull { player.inventory[it] == empty || player.inventory[it].isSameAs(clickedSlotItem) } ?: (0..26).firstOrNull { player.inventory[it] == empty || player.inventory[it].isSameAs(clickedSlotItem) }
                    } else {
                        (0..8).firstOrNull { player.inventory[it] == empty || player.inventory[it].isSameAs(clickedSlotItem) }
                    }

//                    player.sendMessage("<pink>$suitableSlotIndex")
                    if(suitableSlotIndex == null) return
                    val existingItem = player.inventory[suitableSlotIndex].clone()

                    if(player.inventory[properSlot].isSameAs(empty)) return

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
                    return
                }

                if(action == DropButtonAction.DROP) {
                    val existingItem = player.inventory[properSlot].clone()
                    if(existingItem.isSameAs(empty)) return

                    val newItem = if(existingItem.amount == 1) empty else existingItem.clone().apply { amount -= 1 }
                    player.inventory.drop(existingItem.apply { amount = 1 }, isEntireStack = false, isHeld = false)
                    player.inventory[properSlot] = newItem
                }
                if(action == DropButtonAction.CONTROL_DROP) {
                    val existingItem = player.inventory[properSlot].clone()
                    if(existingItem.isSameAs(empty)) return

                    player.inventory.drop(existingItem, isEntireStack = false, isHeld = false)
                    player.inventory[properSlot] = empty
                }
            }
            if(mode == ContainerClickMode.SLOT_DRAG) {
                val action = DragButtonAction.entries.find { it.button == button }
                //TODO Maybe another day I will implement this but its too pain for my little brain and
                // I have spent more than enough time on this already
                player.inventory.sendFullInventoryUpdate()

//            if(action == null) {
//                player.sendMessage("<red>action is null!")
//                return
//            }
//            player.sendMessage("<gray>$properSlot <dark_gray>| <orange>${action.name} <dark_gray>| <aqua>${button}")
//            if(action == DragButtonAction.STARTING_LEFT_MOUSE_DRAG) {
//                player.inventory.dragData = DragButtonInventoryActionData(DragButtonInventoryAction.LEFT, player.inventory.carriedItem)
//                player.sendMessage("<lime>New drag data")
//            }
//
//            if(action == DragButtonAction.ADD_SLOT_FOR_LEFT_MOUSE_DRAG) {
//                if(player.inventory.dragData == null) {
//                    player.sendMessage("<red>drag data is null ADD_SLOT_FOR_LEFT_MOUSE_DRAG")
//                    return
//                }
//                player.inventory.dragData!!.slots.add(properSlot)
//                player.sendMessage("<yellow>Added new slot: $properSlot")
//            }
//
//            if(action == DragButtonAction.ENDING_LEFT_MOUSE_DRAG) {
//                if(player.inventory.dragData == null) {
//                    player.sendMessage("<red>drag data is null ENDING_LEFT_MOUSE_DRAG")
//                    return
//                }
//                val dragData = player.inventory.dragData!!
//                val slots = dragData.slots
//
//                val itemsPerSlot = dragData.item.clone().amount / slots.size
//                val leftover = dragData.item.clone().amount % slots.size
//
//                slots.forEach { player.inventory[it] = dragData.item.clone().apply { amount = itemsPerSlot } }
//                player.inventory.carriedItem = if(leftover > 0) dragData.item.clone().apply { amount = leftover } else empty
//
//                // set the actual slots via player.inventory[slot int] = even amount of items from one item (player.inventory.dragData.item)
//                // player.inventory.carriedItem = leftover item
//            }

                player.inventory.sendFullInventoryUpdate()
            }
        }
        player.inventory.sendFullInventoryUpdate()
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
            for (i in 0 until arraySize) {
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

enum class DragButtonAction(val button: Int, val outsideInv: Boolean = false) {
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