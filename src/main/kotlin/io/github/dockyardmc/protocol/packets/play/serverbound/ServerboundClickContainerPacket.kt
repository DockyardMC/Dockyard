package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.InventoryClickEvent
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.readVarIntEnum
import io.github.dockyardmc.inventory.InventoryClickHandler
import io.github.dockyardmc.inventory.PlayerInventoryUtils
import io.github.dockyardmc.item.*
import io.github.dockyardmc.maths.randomFloat
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.ui.DrawableItemStack
import io.github.dockyardmc.utils.getPlayerEventContext
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import kotlin.math.ceil

class ServerboundClickContainerPacket(
    var windowId: Int,
    var stateId: Int,
    var slot: Int,
    var button: Int,
    var mode: ContainerClickMode,
    var changedSlots: MutableMap<Int, ItemStack>,
    var item: ItemStack,
) : ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        val player = processor.player
        val properSlot = PlayerInventoryUtils.convertPlayerInventorySlot(slot, PlayerInventoryUtils.OFFSET)

        val clickedSlotItem = player.inventory[properSlot].clone()
        val empty = ItemStack.AIR

        val drawableClickType = getDrawableClick(mode)

        if (player.currentlyOpenScreen != null && properSlot >= 0) {
            player.currentlyOpenScreen!!.onClick(slot, player, drawableClickType)
        }

        Events.dispatch(InventoryClickEvent(player, getPlayerEventContext(player)))

        if (windowId == 0) {
            if (mode == ContainerClickMode.NORMAL) {
                var action = NormalButtonAction.entries.find { it.button == button }

                if (slot == -999) {
                    action = when (action) {
                        NormalButtonAction.RIGHT_MOUSE_CLICK -> NormalButtonAction.RIGHT_CLICK_OUTSIDE_INVENTORY
                        NormalButtonAction.LEFT_MOUSE_CLICK -> NormalButtonAction.LEFT_CLICK_OUTSIDE_INVENTORY
                        else -> action
                    }
                }

                if (action == null) {
                    return
                }

                if (action == NormalButtonAction.LEFT_CLICK_OUTSIDE_INVENTORY) {

                    val cursor = player.inventory.cursorItem.value
                    if (!cursor.isEmpty()) {
                        val cancelled = player.inventory.drop(cursor)
                        if (cancelled) {
                            player.inventory.sendFullInventoryUpdate()
                            return
                        }
                        player.inventory.cursorItem.value = empty
                        return
                    }
                }

                if (action == NormalButtonAction.LEFT_MOUSE_CLICK) {

                    if (handleClickEquipAndUnequip(player, properSlot, clickedSlotItem)) return
                    if (handleOffhandClick(player, properSlot, clickedSlotItem, false)) return

                    val clickResult = InventoryClickHandler.handleLeftClick(
                        player,
                        player.inventory,
                        properSlot,
                        clickedSlotItem,
                        player.inventory.cursorItem.value
                    )

                    if (clickResult.cancelled) {
                        player.inventory.sendFullInventoryUpdate()
                        return
                    }

                    player.inventory[properSlot] = clickResult.clicked
                    player.inventory.cursorItem.value = clickResult.cursor
                    return
                }

                if (action == NormalButtonAction.RIGHT_CLICK_OUTSIDE_INVENTORY) {
                    val cursor = player.inventory.cursorItem.value
                    if (!cursor.isEmpty()) {
                        val cancelled = player.inventory.drop(cursor.withAmount(1))
                        if (cancelled) {
                            player.inventory.sendFullInventoryUpdate()
                            return
                        }
                        val newItem =
                            if (cursor.amount - 1 == 0) ItemStack.AIR else cursor.withAmount(cursor.amount - 1)
                        player.inventory.cursorItem.value = newItem
                        return
                    }
                }

                if (action == NormalButtonAction.RIGHT_MOUSE_CLICK) {

                    if (handleClickEquipAndUnequip(player, properSlot, clickedSlotItem)) return
                    if (handleOffhandClick(player, properSlot, clickedSlotItem, true)) return

                    val clickResult = InventoryClickHandler.handleRightClick(
                        player,
                        player.inventory,
                        properSlot,
                        clickedSlotItem,
                        player.inventory.cursorItem.value
                    )

                    if (clickResult.cancelled) {
                        player.inventory.sendFullInventoryUpdate()
                        return
                    }

                    player.inventory[properSlot] = clickResult.clicked
                    player.inventory.cursorItem.value = clickResult.cursor
                    return
                }
            }

            if (mode == ContainerClickMode.NORMAL_SHIFT) {
                val action = NormalShiftButtonAction.entries.find { it.button == button }
                if (action == null) {
                    return
                }

                if (action == NormalShiftButtonAction.SHIFT_LEFT_MOUSE_CLICK || action == NormalShiftButtonAction.SHIFT_RIGHT_MOUSE_CLICK) {

                    var equipmentSlot: EquipmentSlot? = null
                    val equipmentComponent = clickedSlotItem.components.getOrNull<EquippableItemComponent>(EquippableItemComponent::class)

                    if (equipmentComponent == null) {
                        val defaultEquipment = PlayerInventoryUtils.getDefaultEquipmentSlot(clickedSlotItem.material)
                        if (defaultEquipment != null) equipmentSlot = defaultEquipment
                    } else {
                        if (equipmentComponent.allowedEntities.contains(player.type)) equipmentSlot =
                            equipmentComponent.slot
                    }

                    if (EquipmentSlot.isBody(equipmentSlot)) {
                        val current = player.equipment.values.getOrDefault(equipmentSlot, ItemStack.AIR)
                        if (current.isEmpty() && !clickedSlotItem.isEmpty()) {
                            equip(player, properSlot, equipmentSlot!!, clickedSlotItem, equipmentComponent)
                            player.inventory[properSlot] = ItemStack.AIR
                            return
                        }
                    }

                    val clickedEquipmentSlot = player.inventory.getEquipmentSlot(properSlot, player.heldSlotIndex.value)
                    if ((clickedEquipmentSlot != null && clickedEquipmentSlot != EquipmentSlot.MAIN_HAND) && (player.equipment[clickedEquipmentSlot] != null && player.equipment[clickedEquipmentSlot] != empty) && !player.equipment[clickedEquipmentSlot]!!.isEmpty()) {
                        unequip(player, clickedEquipmentSlot, equipmentComponent)
                        val giveInventory = player.inventory.give(clickedSlotItem, 9 to 35)
                        if (!giveInventory) player.inventory.give(clickedSlotItem, 0 to 8)
                        return
                    }

                    val range = if (properSlot <= 8) 9 to 35 else 0 to 8
                    val suitableSlotIndex: Int = InventoryClickHandler.findSuitableSlotInRange(player.inventory, range.first, range.second, item) ?: return

                    val existingItem = player.inventory[suitableSlotIndex].clone()

                    if (clickedSlotItem.isSameAs(empty)) return

                    val shouldStack = !existingItem.isSameAs(empty) &&
                            existingItem.isSameAs(clickedSlotItem) &&
                            (existingItem.amount + clickedSlotItem.amount) <= existingItem.maxStackSize

                    if (shouldStack) {
                        player.inventory[properSlot] = empty
                        player.inventory[suitableSlotIndex] =
                            existingItem.withAmount(existingItem.amount + clickedSlotItem.amount)

                    } else {

                        val isSameItemButCantFullyStack =
                            existingItem.isSameAs(clickedSlotItem) && (existingItem.amount + clickedSlotItem.amount) > existingItem.maxStackSize
                        if (isSameItemButCantFullyStack) {
                            //is the same item but cant stack everything, so we only stack what we can and leave the rest
                            val totalAmount = existingItem.amount + clickedSlotItem.amount
                            val newClicked = existingItem.maxStackSize
                            val remainder = totalAmount - existingItem.maxStackSize

                            player.inventory[properSlot] = clickedSlotItem.withAmount(remainder)
                            player.inventory[suitableSlotIndex] = existingItem.withAmount(newClicked)

                        } else {
                            player.inventory[properSlot] = empty
                            player.inventory[suitableSlotIndex] = clickedSlotItem
                        }
                    }
                }
            }

            if (mode == ContainerClickMode.HOTKEY) {
                val action = if (button == 40) HotkeyButtonAction.OFFHAND_SWAP else HotkeyButtonAction.CHANGE_TO_SLOT

                if (action == HotkeyButtonAction.CHANGE_TO_SLOT) {
                    val existingItem = player.inventory[properSlot].clone()
                    val swappedItem = player.inventory[button].clone()

                    player.inventory[properSlot] = swappedItem
                    player.inventory[button] = existingItem
                }
                if (action == HotkeyButtonAction.OFFHAND_SWAP) {
                    val existingItem = player.inventory[properSlot].clone()
                    val swappedItem = player.inventory[PlayerInventoryUtils.OFFHAND_SLOT].clone()

                    player.inventory[properSlot] = swappedItem
                    player.inventory[PlayerInventoryUtils.OFFHAND_SLOT] = existingItem
                    player.equipment[EquipmentSlot.OFF_HAND] = existingItem
                }
            }

            if (mode == ContainerClickMode.DOUBLE_CLICK) {
                val action = DoubleClickButtonAction.entries.find { it.button == button } ?: return

                if (action == DoubleClickButtonAction.DOUBLE_CLICK) {
                    val cursor = player.inventory.cursorItem.value
                    if (cursor == ItemStack.AIR) return

                    var currentStackSize = cursor.amount

                    player.inventory.slots.values.toList().sortedBy { it.second.amount }.forEach { (slot, itemStack) ->
                        if (itemStack.isSameAs(cursor)) {
                            if (currentStackSize + itemStack.amount >= cursor.maxStackSize) {
                                if (cursor.maxStackSize == currentStackSize) return@forEach

                                val spaceAvailable = cursor.maxStackSize - currentStackSize
                                if (spaceAvailable <= 0) return@forEach

                                val totalAmount = itemStack.amount + currentStackSize
                                currentStackSize = cursor.maxStackSize
                                val remainder = totalAmount - cursor.maxStackSize
                                val newItem = if (remainder == 0) ItemStack.AIR else itemStack.withAmount(remainder)
                                player.inventory[slot] = newItem
                            } else {
                                player.inventory.slots[slot] = ItemStack.AIR
                                currentStackSize += itemStack.amount
                            }
                        }
                    }

                    player.inventory.cursorItem.value = cursor.withAmount(currentStackSize)
                }
            }

            if (mode == ContainerClickMode.DROP) {
                val action = DropButtonAction.entries.find { it.button == button } ?: return

                if (action == DropButtonAction.DROP) {
                    val existingItem = player.inventory[properSlot].clone()
                    if (existingItem.isSameAs(empty)) return

                    val cancelled = player.inventory.drop(existingItem.withAmount(1))
                    if (cancelled) {
                        player.inventory.sendFullInventoryUpdate()
                        return
                    }

                    val newItem =
                        if (existingItem.amount - 1 == 0) empty else existingItem.withAmount(existingItem.amount - 1)
                    player.inventory[properSlot] = newItem
                }
                if (action == DropButtonAction.CONTROL_DROP) {
                    val existingItem = player.inventory[properSlot].clone()
                    if (existingItem.isSameAs(empty)) return

                    val cancelled = player.inventory.drop(existingItem)
                    if (cancelled) {
                        player.inventory.sendFullInventoryUpdate()
                        return
                    }

                    player.inventory[properSlot] = empty
                }
            }
            if (mode == ContainerClickMode.SLOT_DRAG) {
                val action = DragButtonAction.entries.find { it.button == button }
                if (action == null) {
                    return
                }

                if (action.name.contains("end")) {
                    player.inventory.sendFullInventoryUpdate()
                    player.inventory.cursorItem.value = player.inventory.cursorItem.value
                    return
                }

                //TODO Maybe another day I will implement this but its too pain for my little brain and
                // I have spent more than enough time on this already
            }
        }
        player.inventory.sendFullInventoryUpdate()
        player.inventory.cursorItem.value = player.inventory.cursorItem.value
    }

    fun getEquipmentSlot(item: ItemStack): Pair<EquipmentSlot?, EquippableItemComponent?> {
        val component = item.components.getOrNull<EquippableItemComponent>(EquippableItemComponent::class)
        if (component != null) return component.slot to component

        return PlayerInventoryUtils.getDefaultEquipmentSlot(item.material) to null
    }

    fun handleOffhandClick(player: Player, properSlot: Int, clicked: ItemStack, isRightClick: Boolean): Boolean {

        val equipmentSlot = player.inventory.getEquipmentSlot(properSlot, player.heldSlotIndex.value)
        if (equipmentSlot != null && equipmentSlot == EquipmentSlot.OFF_HAND) {

            val offhandItem = player.equipment.values.getOrDefault(equipmentSlot, ItemStack.AIR)
            val cursor = player.inventory.cursorItem.value

            val cursorAmount = if (isRightClick) 1 else cursor.amount

            if (!offhandItem.isEmpty()) {
                if (cursor.isEmpty()) {

                    if (isRightClick) {

                        if (offhandItem.amount == 1) {
                            player.inventory.cursorItem.value = offhandItem
                            player.equipment[EquipmentSlot.OFF_HAND] = ItemStack.AIR
                            return true
                        }

                        val amount = ceil(offhandItem.amount.toDouble() / 2.0).toInt()
                        player.inventory.cursorItem.value = offhandItem.withAmount(amount)
                        player.equipment[EquipmentSlot.OFF_HAND] = offhandItem.withAmount(clicked.amount - amount)
                        return true

                    } else {

                        player.inventory.cursorItem.value = offhandItem
                        player.equipment[EquipmentSlot.OFF_HAND] = ItemStack.AIR
                        return true
                    }

                } else {

                    if (offhandItem.isSameAs(cursor)) {
                        //same item

                        val canStack = offhandItem.amount != offhandItem.maxStackSize &&
                                offhandItem.amount + cursorAmount <= offhandItem.maxStackSize

                        if (canStack) {
                            //can fully stack
                            player.equipment[EquipmentSlot.OFF_HAND] =
                                offhandItem.withAmount(offhandItem.amount + cursorAmount)
                            if (isRightClick) {
                                val newItem =
                                    if (cursor.amount - cursorAmount <= 0) ItemStack.AIR else cursor.withAmount(cursor.amount - cursorAmount)
                                player.inventory.cursorItem.value = newItem
                            } else {
                                player.inventory.cursorItem.value = ItemStack.AIR
                            }
                            return true
                        } else {
                            if (offhandItem.amount != offhandItem.maxStackSize) {
                                // can partially stack
                                val totalAmount = offhandItem.amount + cursorAmount
                                val newClicked = offhandItem.maxStackSize
                                val remainder = totalAmount - offhandItem.maxStackSize

                                player.equipment[EquipmentSlot.OFF_HAND] = offhandItem.withAmount(newClicked)
                                player.inventory.cursorItem.value = cursor.withAmount(remainder)
                                return true
                            } else {
                                // swap
                                player.equipment[EquipmentSlot.OFF_HAND] = cursor
                                player.inventory.cursorItem.value = offhandItem
                                return true
                            }
                        }
                    } else {
                        // items are not the same, swap them
                        player.equipment[EquipmentSlot.OFF_HAND] = cursor
                        player.inventory.cursorItem.value = offhandItem
                        return true
                    }
                }
            } else {
                // offhand is empty
                if (cursor.isEmpty()) return true
                if (isRightClick) {
                    player.equipment[EquipmentSlot.OFF_HAND] = cursor.withAmount(cursorAmount)
                    if (cursor.amount - cursorAmount <= 0) {
                        player.inventory.cursorItem.value = ItemStack.AIR
                    } else {
                        player.inventory.cursorItem.value = cursor.withAmount(cursor.amount - cursorAmount)
                    }
                    return true
                } else {
                    player.equipment[EquipmentSlot.OFF_HAND] = cursor
                    player.inventory.cursorItem.value = ItemStack.AIR
                    return true
                }
            }
        }

        return false
    }

    fun handleClickEquipAndUnequip(player: Player, properSlot: Int, clickedSlotItem: ItemStack): Boolean {
        val equipmentSlot = player.inventory.getEquipmentSlot(properSlot, player.heldSlotIndex.value) ?: return false
        if (!EquipmentSlot.isBody(equipmentSlot)) return false

        val cursorItem = player.inventory.cursorItem.value
        val cursorItemEquipmentSlot = getEquipmentSlot(cursorItem)

        if (!cursorItem.isEmpty() && cursorItemEquipmentSlot.first == null) return false

        if (!cursorItem.isEmpty() && cursorItemEquipmentSlot.first != equipmentSlot) {
            player.inventory.sendFullInventoryUpdate()
            return true
        }

        player.equipment[equipmentSlot] = cursorItem
        player.inventory.cursorItem.value = clickedSlotItem

        return true
    }

    private fun equip(
        player: Player,
        clickedSlot: Int,
        equipmentSlot: EquipmentSlot,
        item: ItemStack,
        component: EquippableItemComponent?
    ) {
        player.equipment[equipmentSlot] = item
        player.inventory[clickedSlot] = item

        val sound = component?.equipSound?.identifier ?: Sounds.ITEM_ARMOR_EQUIP_GENERIC
        player.playSound(sound, pitch = randomFloat(1.0f, 1.2f))
    }

    private fun unequip(player: Player, equipmentSlot: EquipmentSlot, component: EquippableItemComponent?) {
        player.equipment[equipmentSlot] = ItemStack.AIR
        player.inventory[player.inventory.getSlotId(equipmentSlot, player.heldSlotIndex.value)] = ItemStack.AIR

        val sound = component?.equipSound?.identifier ?: Sounds.ITEM_ARMOR_EQUIP_GENERIC
        player.playSound(sound, pitch = randomFloat(0.6f, 0.8f))
    }

    private fun getDrawableClick(mode: ContainerClickMode): DrawableItemStack.ClickType {
        var drawableClickType: DrawableItemStack.ClickType = DrawableItemStack.ClickType.LEFT_CLICK
        when (mode) {
            ContainerClickMode.NORMAL -> {
                val action = NormalButtonAction.entries.find { it.button == button }
                    ?: throw IllegalArgumentException("Button $button is not part of NormalButtonAction")
                drawableClickType = when (action) {
                    NormalButtonAction.LEFT_MOUSE_CLICK -> DrawableItemStack.ClickType.LEFT_CLICK
                    NormalButtonAction.RIGHT_MOUSE_CLICK -> DrawableItemStack.ClickType.RIGHT_CLICK
                    NormalButtonAction.LEFT_CLICK_OUTSIDE_INVENTORY -> DrawableItemStack.ClickType.LEFT_CLICK_OUTSIDE_INVENTORY
                    NormalButtonAction.RIGHT_CLICK_OUTSIDE_INVENTORY -> DrawableItemStack.ClickType.RIGHT_CLICK_OUTSIDE_INVENTORY
                }
            }

            ContainerClickMode.NORMAL_SHIFT -> {
                val action = NormalShiftButtonAction.entries.find { it.button == button }
                    ?: throw IllegalArgumentException("Button $button is not part of NormalShiftButtonAction")
                drawableClickType = when (action) {
                    NormalShiftButtonAction.SHIFT_LEFT_MOUSE_CLICK -> DrawableItemStack.ClickType.LEFT_CLICK_SHIFT
                    NormalShiftButtonAction.SHIFT_RIGHT_MOUSE_CLICK -> DrawableItemStack.ClickType.RIGHT_CLICK_SHIFT
                }
            }

            ContainerClickMode.HOTKEY -> {
                val action = if (button == 40) HotkeyButtonAction.OFFHAND_SWAP else HotkeyButtonAction.CHANGE_TO_SLOT
                drawableClickType =
                    if (action == HotkeyButtonAction.OFFHAND_SWAP) DrawableItemStack.ClickType.OFFHAND else DrawableItemStack.ClickType.HOTKEY
            }

            ContainerClickMode.MIDDLE_CLICK -> drawableClickType = DrawableItemStack.ClickType.MIDDLE_CLICK
            ContainerClickMode.DROP -> drawableClickType = DrawableItemStack.ClickType.DROP
            ContainerClickMode.SLOT_DRAG -> {
                val action = DragButtonAction.entries.find { it.button == button }
                drawableClickType = when (action) {
                    DragButtonAction.STARTING_LEFT_MOUSE_DRAG,
                    DragButtonAction.ADD_SLOT_FOR_LEFT_MOUSE_DRAG,
                    DragButtonAction.ENDING_LEFT_MOUSE_DRAG -> DrawableItemStack.ClickType.LEFT_CLICK

                    DragButtonAction.STARTING_RIGHT_MOUSE_DRAG,
                    DragButtonAction.ENDING_RIGHT_MOUSE_DRAG,
                    DragButtonAction.ADD_SLOT_FOR_RIGHT_MOUSE_DRAG -> DrawableItemStack.ClickType.RIGHT_CLICK

                    DragButtonAction.STARTING_MIDDLE_MOUSE_DRAG,
                    DragButtonAction.ADD_SLOT_FOR_MIDDLE_MOUSE_DRAG,
                    DragButtonAction.ENDING_MIDDLE_MOUSE_DRAG -> DrawableItemStack.ClickType.MIDDLE_CLICK

                    null -> throw IllegalStateException("action with button $button of DragButtonAction not set")
                }
            }

            ContainerClickMode.DOUBLE_CLICK -> drawableClickType = DrawableItemStack.ClickType.LEFT_CLICK
        }
        return drawableClickType
    }

    companion object {
        fun read(buffer: ByteBuf): ServerboundClickContainerPacket {
            val windowsId = buffer.readVarInt()
            val stateId = buffer.readVarInt()
            val slot = buffer.readShort().toInt()
            val button = buffer.readByte().toInt()
            val mode = buffer.readVarIntEnum<ContainerClickMode>()
            val changedSlots = mutableMapOf<Int, ItemStack>()

            val arraySize = buffer.readVarInt()
            for (i in 0 until arraySize) {
                val slotNumber = buffer.readShort().toInt()
                val slotData = ItemStack.read(buffer)
                changedSlots[slotNumber] = slotData
            }

            val carriedItem = ItemStack.read(buffer)

            val rest = buffer.readableBytes()
            buffer.readBytes(rest)
            buffer.clear()

            return ServerboundClickContainerPacket(
                windowsId,
                stateId,
                slot,
                button,
                mode,
                changedSlots,
                carriedItem
            )
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

enum class DoubleClickButtonAction(val button: Int, val outsideInv: Boolean = false) {
    DOUBLE_CLICK(0),
    PICKUP_ALL(1)
}