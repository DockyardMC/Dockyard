package io.github.dockyardmc.inventory

import io.github.dockyardmc.item.*
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.utils.isBetween
import kotlin.math.ceil

object InventoryClickHandler {

    fun leftClick(player: Player, slot: Int): Boolean {
        val converted = PlayerInventoryUtils.convertPlayerInventorySlot(slot, player.heldSlotIndex.value)
        val cursor = player.inventory.cursorItem.value
        val clicked = player.inventory[converted]
        val clickResult = handleLeftClick(player, player.inventory, slot, clicked, cursor)

        if(clickResult.cancelled) {
            return false
        }

        player.inventory[converted] = clickResult.clicked
        player.inventory.cursorItem.value = clickResult.cursor
        return true
    }

    fun rightClick(player: Player, slot: Int): Boolean {
        val converted = PlayerInventoryUtils.convertPlayerInventorySlot(slot, player.heldSlotIndex.value)
        val cursor = player.inventory.cursorItem.value
        val clicked = player.inventory[converted]
        val clickResult = handleLeftClick(player, player.inventory, slot, clicked, cursor)

        if(clickResult.cancelled) {
            return false
        }

        player.inventory[converted] = clickResult.clicked
        player.inventory.cursorItem.value = clickResult.cursor
        return true
    }

    fun handleLeftClick(player: Player, inventory: PlayerInventory, slot: Int, clicked: ItemStack, cursor: ItemStack): InventoryClickResult {
        val result = InventoryClickResult(clicked, cursor, false)

        if(cursor.isSameAs(clicked)) {
            //try to stack
            val totalAmount = cursor.amount + clicked.amount
            val maxSize = cursor.maxStackSize.value

            if(!isBetween(totalAmount, 0, clicked.maxStackSize.value)) {
                //too big, stuck only amount you can
                result.cursor = cursor.clone().apply { amount = totalAmount - maxSize }
                result.clicked = clicked.clone().apply { amount = maxSize }
            } else {
                //merge
                result.cursor = ItemStack.AIR
                result.clicked = clicked.clone().apply { amount = totalAmount }
            }

        } else {
            //swap cause they are not the same
            result.cursor = clicked
            result.clicked = cursor
        }

        return result
    }

    fun handleRightClick(player: Player, inventory: PlayerInventory, slot: Int, clicked: ItemStack, cursor: ItemStack): InventoryClickResult {
        val result = InventoryClickResult(clicked, cursor, false)

        if(clicked.isSameAs(cursor)) {
            //can be stacked
            val amount = clicked.amount + 1
            if(isBetween(amount, 0, clicked.maxStackSize.value)){
                //too large
                return result
            } else {
                //add 1 to clicked
                result.cursor = cursor.clone().apply { this.amount -= 1 }
                result.clicked = clicked.clone().apply { this.amount = amount }
            }
        } else {
            // cant stack
            if(cursor.isEmpty()) {
                //take half
                val amount = ceil(clicked.amount.toDouble() / 2.0).toInt()
                result.cursor = clicked.withAmount(amount)
                result.clicked = clicked.withAmount { it / 2 }
            } else {
                if(clicked.isEmpty()) {
                    // put 1 to clicked
                    result.cursor = cursor.withAmount { it - 1}
                    result.clicked = cursor.withAmount(1)
                } else {
                    // swap items
                    result.cursor = clicked
                    result.clicked = cursor
                }
            }
        }
        return result
    }

    fun handleChangeHeld(player: Player, inventory: PlayerInventory, slot: Int, clicked: ItemStack, cursor: ItemStack): InventoryClickResult {
        val result = InventoryClickResult(clicked, cursor, false)

        result.clicked = cursor
        result.cursor = clicked

        return result
    }

    fun handleShiftClick(player: Player, inventory: PlayerInventory, slot: Int, clicked: ItemStack, cursor: ItemStack): InventoryClickResult {
        val result = InventoryClickResult(clicked, cursor, false)

        if(clicked.isEmpty()) return result.cancelled()

        //armor equip
        if(inventory.getWindowId() == 0.toByte()) {
            var equipmentSlot: EquipmentSlot? = null
            val equipmentComponent = clicked.components.getOrNull<EquippableItemComponent>(EquippableItemComponent::class)
            if(equipmentComponent == null) {
                val defaultEquipment = PlayerInventoryUtils.getDefaultEquipmentSlot(clicked.material)
                if(defaultEquipment != null) equipmentSlot = defaultEquipment
            } else {
                if(equipmentComponent.allowedEntities.contains(player.type)) equipmentSlot = equipmentComponent.slot
            }

            if(equipmentSlot != null) {
                val current = player.equipment.values.getOrDefault(equipmentSlot, ItemStack.AIR) //TODO redo equipment
                if(current.isEmpty()) {
                    result.clicked = ItemStack.AIR
                    result.cursor = cursor
                    //TODO set equipment
                }
                return result
            }
        }

        result.cancelled = true
        //TODO shit clicking to other inventory

        return result
    }
}