package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.readVarIntEnum
import io.github.dockyardmc.inventory.InventoryClickHandler
import io.github.dockyardmc.inventory.PlayerInventoryUtils
import io.github.dockyardmc.inventory.give
import io.github.dockyardmc.item.*
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.ui.DrawableClickType
import io.github.dockyardmc.ui.DrawableContainerScreen
import io.github.dockyardmc.utils.randomFloat
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
        val currentInventory = player.currentOpenInventory
        val properSlot = PlayerInventoryUtils.convertPlayerInventorySlot(slot, PlayerInventoryUtils.OFFSET)

        val clickedSlotItem = player.inventory[properSlot].clone()
        val empty = ItemStack.AIR

        val drawableClickType = getDrawableClick(mode)

        if (currentInventory != null && currentInventory is DrawableContainerScreen && properSlot >= 0) currentInventory.click(
            slot,
            player,
            drawableClickType
        )

        if (windowId == 0) {
            if (mode == ContainerClickMode.NORMAL) {
                val action = NormalButtonAction.entries.find { it.button == button }
                if (action == null) {
                    return
                }
                if (action == NormalButtonAction.LEFT_MOUSE_CLICK) {

                    // drop the item
                    if (slot == -999) {

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

                if (action == NormalButtonAction.RIGHT_MOUSE_CLICK) {

                    if (slot == -999) {
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
                    val equipmentComponent =
                        clickedSlotItem.components.getOrNull<EquippableItemComponent>(EquippableItemComponent::class)
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
                            return
                        }
                    }

                    val clickedEquipmentSlot = player.inventory.getEquipmentSlot(properSlot, player.heldSlotIndex.value)
                    if ((clickedEquipmentSlot != null && clickedEquipmentSlot != EquipmentSlot.MAIN_HAND) && (player.equipment[clickedEquipmentSlot] != null && player.equipment[clickedEquipmentSlot] != empty) && !player.equipment[clickedEquipmentSlot]!!.isEmpty()) {
                        unequip(player, clickedEquipmentSlot, equipmentComponent)
                        return
                    }

                    var suitableSlotIndex: Int? = null

                    if (properSlot <= 8) {
                        //we are in hotbar

                        //find slot in the rest of the inventory
                        for (i in 9 until 35) {
                            val item = player.inventory[i]
                            if (item == empty || (item.isSameAs(clickedSlotItem) && item.amount != item.maxStackSize.value)) {
                                suitableSlotIndex = i
                                break
                            }
                        }

                    } else {
                        // we are elsewhere in inventory

                        //find slot in the hotbar
                        for (i in 0 until 8) {
                            val item = player.inventory[i]
                            if (item == empty || (item.isSameAs(clickedSlotItem) && item.amount != item.maxStackSize.value)) {
                                suitableSlotIndex = i
                                break
                            }
                        }
                    }

                    if (suitableSlotIndex == null) return
                    val existingItem = player.inventory[suitableSlotIndex].clone()

                    if (clickedSlotItem.isSameAs(empty)) return

                    val shouldStack = !existingItem.isSameAs(empty) &&
                            existingItem.isSameAs(clickedSlotItem) &&
                            (existingItem.amount + clickedSlotItem.amount) <= existingItem.maxStackSize.value

                    if (shouldStack) {
                        player.inventory[properSlot] = empty
                        player.inventory[suitableSlotIndex] =
                            existingItem.withAmount(existingItem.amount + clickedSlotItem.amount)

                    } else {

                        val isSameItemButCantFullyStack =
                            existingItem.isSameAs(clickedSlotItem) && (existingItem.amount + clickedSlotItem.amount) > existingItem.maxStackSize.value
                        if (isSameItemButCantFullyStack) {
                            //is the same item but cant stack everything so we only stack what we can and leave the rest
                            val totalAmount = existingItem.amount + clickedSlotItem.amount
                            val newClicked = existingItem.maxStackSize.value
                            val remainder = totalAmount - existingItem.maxStackSize.value

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
            if (mode == ContainerClickMode.DROP) {
                val action = DropButtonAction.entries.find { it.button == button }
                if (action == null) {
                    return
                }

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

                        val canStack = offhandItem.amount != offhandItem.maxStackSize.value &&
                                offhandItem.amount + cursorAmount <= offhandItem.maxStackSize.value

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
                            if (offhandItem.amount != offhandItem.maxStackSize.value) {
                                // can partially stack
                                val totalAmount = offhandItem.amount + cursorAmount
                                val newClicked = offhandItem.maxStackSize.value
                                val remainder = totalAmount - offhandItem.maxStackSize.value

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
        val equipmentSlot = player.inventory.getEquipmentSlot(properSlot, player.heldSlotIndex.value)
        if (EquipmentSlot.isBody(equipmentSlot)) {
            if (!player.inventory.cursorItem.value.isEmpty() && clickedSlotItem.isEmpty()) {
                //clicking into armor slot, players cursor item is not empty
                val cursorEquipmentSlot = getEquipmentSlot(player.inventory.cursorItem.value)
                if (cursorEquipmentSlot.first == equipmentSlot!!) {
                    equip(
                        player,
                        properSlot,
                        equipmentSlot,
                        player.inventory.cursorItem.value,
                        cursorEquipmentSlot.second
                    )
                    player.inventory.cursorItem.value = ItemStack.AIR
                    return true
                } else {
                    player.inventory.sendFullInventoryUpdate()
                    return true
                }
            }
            if (player.inventory.cursorItem.value.isEmpty() && player.equipment.values.getOrDefault(equipmentSlot!!, ItemStack.AIR) != ItemStack.AIR) {
                player.inventory.cursorItem.value = player.equipment[equipmentSlot]!!
                unequip(player, equipmentSlot, null)
                return true
            }
        }
        return false
    }

    private fun equip(
        player: Player,
        clickedSlot: Int,
        equipmentSlot: EquipmentSlot,
        item: ItemStack,
        component: EquippableItemComponent?
    ) {
        player.equipment[equipmentSlot] = item
        player.inventory[clickedSlot] = ItemStack.AIR

        val sound = component?.equipSound?.identifier ?: Sounds.ITEM_ARMOR_EQUIP_GENERIC
        player.playSound(sound, pitch = randomFloat(1.0f, 1.2f))
    }

    private fun unequip(player: Player, equipmentSlot: EquipmentSlot, component: EquippableItemComponent?) {
        player.give(player.equipment[equipmentSlot]!!)
        player.equipment[equipmentSlot] = ItemStack.AIR
        player.inventory.sendFullInventoryUpdate()

        val sound = component?.equipSound?.identifier ?: Sounds.ITEM_ARMOR_EQUIP_GENERIC
        player.playSound(sound, pitch = randomFloat(0.6f, 0.8f))
    }

    private fun getDrawableClick(mode: ContainerClickMode): DrawableClickType {
        var drawableClickType: DrawableClickType = DrawableClickType.LEFT_CLICK
        when (mode) {
            ContainerClickMode.NORMAL -> {
                val action = NormalButtonAction.entries.find { it.button == button }
                    ?: throw IllegalArgumentException("Button $button is not part of NormalButtonAction")
                drawableClickType = when (action) {
                    NormalButtonAction.LEFT_MOUSE_CLICK -> DrawableClickType.LEFT_CLICK
                    NormalButtonAction.RIGHT_MOUSE_CLICK -> DrawableClickType.RIGHT_CLICK
                    NormalButtonAction.LEFT_CLICK_OUTSIDE_INVENTORY -> DrawableClickType.LEFT_CLICK_OUTSIDE_INVENTORY
                    NormalButtonAction.RIGHT_CLICK_OUTSIDE_INVENTORY -> DrawableClickType.RIGHT_CLICK_OUTSIDE_INVENTORY
                }
            }

            ContainerClickMode.NORMAL_SHIFT -> {
                val action = NormalShiftButtonAction.entries.find { it.button == button }
                    ?: throw IllegalArgumentException("Button $button is not part of NormalShiftButtonAction")
                drawableClickType = when (action) {
                    NormalShiftButtonAction.SHIFT_LEFT_MOUSE_CLICK -> DrawableClickType.LEFT_CLICK_SHIFT
                    NormalShiftButtonAction.SHIFT_RIGHT_MOUSE_CLICK -> DrawableClickType.RIGHT_CLICK_SHIFT
                }
            }

            ContainerClickMode.HOTKEY -> {
                val action = if (button == 40) HotkeyButtonAction.OFFHAND_SWAP else HotkeyButtonAction.CHANGE_TO_SLOT
                drawableClickType =
                    if (action == HotkeyButtonAction.OFFHAND_SWAP) DrawableClickType.OFFHAND else DrawableClickType.HOTKEY
            }

            ContainerClickMode.MIDDLE_CLICK -> drawableClickType = DrawableClickType.MIDDLE_CLICK
            ContainerClickMode.DROP -> drawableClickType = DrawableClickType.DROP
            ContainerClickMode.SLOT_DRAG -> {
                val action = DragButtonAction.entries.find { it.button == button }
                drawableClickType = when (action) {
                    DragButtonAction.STARTING_LEFT_MOUSE_DRAG,
                    DragButtonAction.ADD_SLOT_FOR_LEFT_MOUSE_DRAG,
                    DragButtonAction.ENDING_LEFT_MOUSE_DRAG -> DrawableClickType.LEFT_CLICK

                    DragButtonAction.STARTING_RIGHT_MOUSE_DRAG,
                    DragButtonAction.ENDING_RIGHT_MOUSE_DRAG,
                    DragButtonAction.ADD_SLOT_FOR_RIGHT_MOUSE_DRAG -> DrawableClickType.RIGHT_CLICK

                    DragButtonAction.STARTING_MIDDLE_MOUSE_DRAG,
                    DragButtonAction.ADD_SLOT_FOR_MIDDLE_MOUSE_DRAG,
                    DragButtonAction.ENDING_MIDDLE_MOUSE_DRAG -> DrawableClickType.MIDDLE_CLICK

                    null -> throw IllegalStateException("action with button $button of DragButtonAction not set")
                }
            }

            ContainerClickMode.DOUBLE_CLICK -> drawableClickType = DrawableClickType.LEFT_CLICK
        }
        return drawableClickType
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundClickContainerPacket {
            val windowsId = buf.readVarInt()
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

enum class DoubleClickButtonAction(button: Int, outsideInv: Boolean = false) {
    DOUBLE_CLICK(0),
    PICKUP_ALL(1)
}