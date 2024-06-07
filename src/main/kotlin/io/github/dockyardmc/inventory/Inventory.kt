package io.github.dockyardmc.inventory

class Inventory {
    val name: String = "Inventory"
    val size = 35 //TODO Change by entity type
    private val slots: MutableMap<Int, ItemStack> = mutableMapOf()

    fun set(slot: Int, item: ItemStack) {
        slots[slot] = item
    }

    fun get(slot: Int): ItemStack {
        return slots[slot] ?: ItemStack.air
    }

    fun clear() {
        slots.clear()
    }

    //TODO
    fun sendInventoryUpdate() {

    }
}