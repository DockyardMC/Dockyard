package io.github.dockyardmc.inventory

import cz.lukynka.Bindable
import cz.lukynka.BindableMap
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerDropItemEvent
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.item.clone
import io.github.dockyardmc.item.isSameAs
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundSetInventorySlotPacket
import io.github.dockyardmc.registry.Items

class Inventory(var entity: Entity) {
    val name: String = "Inventory"
    val size = entity.inventorySize
    val slots: BindableMap<Int, ItemStack> = BindableMap()
    var carriedItem: Bindable<ItemStack> = Bindable(ItemStack.air)
    var offhandItem: Bindable<ItemStack> = Bindable(ItemStack.air)

    var dragData: DragButtonInventoryActionData? = null

    init {
        slots.itemSet {
            sendInventoryUpdate(it.key)
            if(entity is Player && it.key == (entity as Player).selectedHotbarSlot.value) {
                entity.equipment.value = entity.equipment.value.apply { mainHand = it.value }
            }
        }
        slots.itemRemoved {
            if(entity is Player && it.key == (entity as Player).selectedHotbarSlot.value) {
                entity.equipment.value = entity.equipment.value.apply { mainHand = it.value }
            }
            sendInventoryUpdate(it.key)
        }
        carriedItem.valueChanged { sendInventoryUpdate(46) }

        for (i in 0 until entity.inventorySize) {
            set(i, ItemStack.air)
        }
    }

    operator fun set(slot: Int, item: ItemStack) {
        var properItem = item

        if(item.amount == 0 && item.material != Items.AIR) properItem = ItemStack.air
        slots[slot] = properItem
    }

    operator fun get(slot: Int): ItemStack = slots[slot] ?: ItemStack.air

    fun clear() {
        slots.clear(false)
    }

    fun sendInventoryUpdate(slot: Int) {
        val player = entity as Player
        val item = if(slot == 46) carriedItem.value else slots[slot]
        val packet = ClientboundSetInventorySlotPacket(slot, item ?: ItemStack.air)
        player.sendPacket(packet)
    }

    fun sendFullInventoryUpdate() {
        sendInventoryUpdate(46)
        repeat(size) {
            sendInventoryUpdate(it)
        }
        sendInventoryUpdate(size +1)
        sendInventoryUpdate(size)
    }

    fun drop(itemStack: ItemStack, isEntireStack: Boolean, isHeld: Boolean) {
        val player= entity as Player

        val event = PlayerDropItemEvent(player, itemStack)
        Events.dispatch(event)
        if(event.cancelled) {
            sendFullInventoryUpdate()
            return
        }

        if(isHeld) {
            val held = player.getHeldItem(PlayerHand.MAIN_HAND)
            val newItem = if(isEntireStack) {
                if(held.amount == 1) ItemStack.air else held.clone().apply { amount -= 1 }
            } else {
                ItemStack.air
            }
            player.inventory[player.selectedHotbarSlot.value] = newItem
        }
        // let users implement dropping themselves if they need it
    }
}

fun Player.give(itemStack: ItemStack) {
    for (slot in inventory.slots.values) {
        if (slot.value.isSameAs(itemStack) && slot.value.amount < slot.value.maxStackSize.value) {
            val remaining = slot.value.amount + itemStack.amount - slot.value.maxStackSize.value
            if (remaining > 0) {
                inventory[slot.key] = slot.value.apply { amount = slot.value.maxStackSize.value }
                give(itemStack.apply { amount = remaining })
                return
            } else {
                inventory[slot.key] = slot.value.apply { amount += itemStack.amount }
                return
            }
        }
    }

    inventory.slots.values.forEach {
        if(!it.value.isSameAs(ItemStack.air)) return@forEach
        inventory[it.key] = itemStack
        return
    }
}

fun Player.clearInventory() {
    this.inventory.clear()
}


data class DragButtonInventoryActionData(
    val type: DragButtonInventoryAction,
    val item: ItemStack,
    val slots: MutableList<Int> = mutableListOf()
)

enum class DragButtonInventoryAction {
    LEFT,
    RIGHT,
    MIDDLE
}