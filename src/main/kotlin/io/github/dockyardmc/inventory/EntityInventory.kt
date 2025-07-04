package io.github.dockyardmc.inventory

import cz.lukynka.bindables.BindableMap
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.InventoryGiveItemEvent
import io.github.dockyardmc.events.InventoryItemChangeEvent
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.registries.Item
import io.github.dockyardmc.utils.getEntityEventContext

abstract class EntityInventory(val entity: Entity, val size: Int) {
    val slots: BindableMap<Int, ItemStack> = BindableMap()

    abstract fun getWindowId(): Byte

    open operator fun set(slot: Int, item: ItemStack) {
        require(slot in 0..size) { "Inventory does not have slot $slot" }

        val newItem: ItemStack = if (item.amount == 0 && item.material != Items.AIR) {
            ItemStack.AIR
        } else {
            item
        }

        val oldItem = slots[slot] ?: ItemStack.AIR

        val event = InventoryItemChangeEvent(entity, this, slot, newItem, oldItem, getEntityEventContext(entity))
        Events.dispatch(event)

        slots[event.slot] = event.newItem
    }

    open operator fun get(slot: Int): ItemStack = slots[slot] ?: ItemStack.AIR

    open fun clear() {
        slots.values.forEach {
            slots[it.key] = ItemStack.AIR
        }
    }

    open fun give(item: ItemStack, range: Pair<Int, Int> = 0 to 36): Boolean {
        val suitableSlots = mutableListOf<Int>()
        for (i in range.first until range.second) {
            val slot = get(i)
            if (slot.isEmpty()) {
                suitableSlots.add(i)
            } else {
                val canStack = slot == item &&
                        slot.amount != slot.maxStackSize &&
                        slot.amount + item.amount <= slot.maxStackSize

                if (canStack) {
                    suitableSlots.add(i)
                } else {
                    if (slot == item && slot.amount != slot.maxStackSize) {
                        suitableSlots.add(i)
                    }
                }
            }
        }

        // non-empty first so items can stack
        suitableSlots.filter { !get(it).isEmpty() }.forEach { index ->
            val slot = get(index)
            val canStack = slot == item &&
                    slot.amount != slot.maxStackSize &&
                    slot.amount + item.amount <= slot.maxStackSize
            if (canStack) {
                slots[index] = slot.withAmount(slot.amount + item.amount)
                return true
            } else {
                val totalAmount = item.amount + slot.amount
                val newClicked = slot.maxStackSize
                slots[index] = slot.withAmount(newClicked)
                val remainder = totalAmount - slot.maxStackSize
                give(item.withAmount(remainder))
                return true
            }
        }

        // empty slots last
        suitableSlots.filter { get(it).isEmpty() }.forEach { index ->
            slots[index] = item
            Events.dispatch(InventoryGiveItemEvent(this.entity, item, true, getEntityEventContext(this.entity)))
            return true
        }

        Events.dispatch(InventoryGiveItemEvent(this.entity, item, false, getEntityEventContext(this.entity)))
        return false
    }

    fun getAmountOf(itemStack: ItemStack): Int {
        var totalAmount = 0
        slots.values.values.forEach { item ->
            if (item != itemStack) return@forEach
            totalAmount += item.amount
        }
        return totalAmount
    }

    fun getAmountOf(item: Item): Int {
        return getAmountOf(item.toItemStack())
    }

    abstract fun sendInventoryUpdate(slot: Int)
}