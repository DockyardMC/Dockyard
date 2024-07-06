package io.github.dockyardmc.inventory

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundSetInventorySlotPacket
import io.github.dockyardmc.utils.MathUtils

class Inventory(var player: Player) {
    val name: String = "Inventory"
    val size = 35 //TODO Change by entity type
    private val slots: MutableMap<Int, ItemStack> = mutableMapOf()

    operator fun set(slot: Int, item: ItemStack) {
        slots[slot] = item
        player.sendInventoryUpdate(slot)
    }

    fun updateServerSlotData(slot: Int, item: ItemStack) {
        slots[slot] = item
    }

    operator fun get(slot: Int): ItemStack = slots[slot] ?: ItemStack.air

    fun clear() {
        slots.clear()
    }

    //TODO make work
    fun Player.give(itemStack: ItemStack) {

    }

    fun Player.clearInventory() {
        this.inventory.clear()
    }

    //TODO make work
    fun Player.sendInventoryUpdate(slot: Int) {
        val clientSlot =  MathUtils.toOriginalSlotIndex(slot)
        val packet = ClientboundSetInventorySlotPacket(0, 0, clientSlot, slots[slot] ?: ItemStack.air)
        this.sendPacket(packet)
    }
}

