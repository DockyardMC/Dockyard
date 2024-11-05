package io.github.dockyardmc.inventory

import cz.lukynka.BindableMap
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.events.InventoryItemChangeEvent
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.item.isSameAs
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.utils.getEntityEventContext
import io.github.dockyardmc.utils.isBetween

abstract class EntityInventory(val entity: Entity, val size: Int) {
    val slots: BindableMap<Int, ItemStack> = BindableMap()

    abstract fun getWindowId(): Byte

    open operator fun set(slot: Int, item: ItemStack) {
        var newItem: ItemStack = item
        if (!isBetween(slot, 0, size)) throw IllegalArgumentException("Inventory does not have slot $slot")
        if (item.amount == 0 && item.material != Items.AIR) newItem = ItemStack.AIR

        val oldItem = slots[slot] ?: ItemStack.AIR

        val event = InventoryItemChangeEvent(entity, this, slot, newItem, oldItem, getEntityEventContext(entity))
        slots[event.slot] = event.newItem
    }

    open operator fun get(slot: Int): ItemStack = slots[slot] ?: ItemStack.AIR

    open fun clear() {
        slots.clear(false)
    }

    open fun give(item: ItemStack) {
        for (slot in slots.values) {
            if (slot.value.isSameAs(item) && slot.value.amount < slot.value.maxStackSize.value) {
                val remaining = slot.value.amount + item.amount - slot.value.maxStackSize.value
                if (remaining > 0) {
                    this[slot.key] = slot.value.apply { amount = slot.value.maxStackSize.value }
                    give(item.apply { amount = remaining })
                    return
                } else {
                    this[slot.key] = slot.value.apply { amount += item.amount }
                    return
                }
            }
        }

        slots.values.forEach {
            if(!it.value.isSameAs(ItemStack.AIR)) return@forEach
            this[it.key] = item
            return
        }
    }

    abstract fun sendInventoryUpdate(slot: Int)
    abstract fun drop(itemStack: ItemStack, isEntireStack: Boolean, isHeld: Boolean)

}