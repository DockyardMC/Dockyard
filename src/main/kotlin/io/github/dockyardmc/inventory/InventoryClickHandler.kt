package io.github.dockyardmc.inventory

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.item.clone
import io.github.dockyardmc.item.isSameAs
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.maths.isBetween
import kotlin.math.ceil

object InventoryClickHandler {

    fun handleLeftClick(player: Player, inventory: PlayerInventory, slot: Int, clicked: ItemStack, cursor: ItemStack): InventoryClickResult {
        val result = InventoryClickResult(clicked, cursor, false)

        if (cursor.isSameAs(clicked)) {
            //try to stack
            val totalAmount = cursor.amount + clicked.amount
            val maxSize = cursor.maxStackSize

            if (!isBetween(totalAmount, 0, clicked.maxStackSize)) {
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

        if (clicked.isSameAs(cursor)) {
            //can be stacked
            val amount = clicked.amount + 1
            if (!isBetween(amount, 0, clicked.maxStackSize)) {
                //too large
                return result
            } else {
                //add 1 to clicked
                result.cursor = cursor.withAmount(cursor.amount - 1)
                if (cursor.amount - 1 == 0) result.cursor = ItemStack.AIR
                result.clicked = clicked.withAmount(amount)
            }
        } else {
            // cant stack
            if (cursor.isEmpty()) {

                if (clicked.amount == 1) {
                    result.cursor = clicked
                    result.clicked = ItemStack.AIR
                    return result
                }

                //take half
                val amount = ceil(clicked.amount.toDouble() / 2.0).toInt()
                result.cursor = clicked.withAmount(amount)
                result.clicked = clicked.withAmount(clicked.amount - amount)
            } else {
                if (clicked.isEmpty()) {
                    // put 1 to clicked
                    val newCursor = if (cursor.amount - 1 == 0) ItemStack.AIR else cursor.withAmount(cursor.amount - 1)
                    result.cursor = newCursor
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


    fun findSuitableSlotInRange(inventory: EntityInventory, min: Int, max: Int, item: ItemStack): Int? {
        val suitableSlots = mutableListOf<Int>()

        for (i in min until max) {
            val slot = inventory[i]
            if (slot.isEmpty()) {
                suitableSlots.add(i)
            } else {
                val canStack = slot.isSameAs(item) &&
                        slot.amount != slot.maxStackSize &&
                        slot.amount + item.amount <= slot.maxStackSize

                if (canStack) {
                    suitableSlots.add(i)
                } else {
                    if (slot.isSameAs(item) && slot.amount != slot.maxStackSize) {
                        suitableSlots.add(i)
                    }
                }
            }
        }

        val nonEmpty = suitableSlots.filter { !inventory[it].isEmpty() }
        val empty = suitableSlots.filter { inventory[it].isEmpty() }

        // non-empty first so items can stack
        nonEmpty.forEach { index ->
            return index
        }

        // empty slots last
        empty.forEach { index ->
            return index
        }

        return null
    }
}