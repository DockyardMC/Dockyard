package io.github.dockyardmc.inventory

import io.github.dockyardmc.player.Player

class Inventory {
    val name: String = "Inventory"
    val size = 35 //TODO Change by entity type
    private val slots: MutableMap<Int, ItemStack> = mutableMapOf()

    operator fun set(slot: Int, item: ItemStack) {
        slots[slot] = item
    }

    operator fun get(slot: Int): ItemStack = slots[slot] ?: ItemStack.air

    fun clear() {
        slots.clear()
    }
}

//TODO make work
fun Player.give(itemStack: ItemStack) {

}

fun Player.clearInventory() {
    this.inventory.clear()
}

//TODO make work
fun Player.sendInventoryUpdate() {

}