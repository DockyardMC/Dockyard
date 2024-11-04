package io.github.dockyardmc.inventory

import cz.lukynka.Bindable
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerDropItemEvent
import io.github.dockyardmc.events.PlayerEquipEvent
import io.github.dockyardmc.item.EquipmentSlot
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.item.clone
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundSetInventoryCursorPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundSetInventorySlotPacket

class PlayerInventory(var player: Player) : EntityInventory(player, INVENTORY_SIZE) {
    val name: String = "Inventory"

    companion object {
        const val INVENTORY_SIZE: Int = 46
        const val INNER_INVENTORY_SIZE: Int = 36
    }

    var cursorItem: Bindable<ItemStack> = Bindable(ItemStack.AIR)

    override fun getWindowId(): Byte {
        return 0
    }

    override fun clear() {
        cursorItem.value = ItemStack.AIR
        super.clear()

        //TODO Update equipment
    }

    fun getSlotId(slot: EquipmentSlot, heldSlot: Int): Int {
        return when (slot) {
            EquipmentSlot.MAIN_HAND -> heldSlot
            EquipmentSlot.OFF_HAND -> PlayerInventoryUtils.OFFHAND_SLOT
            EquipmentSlot.HEAD -> PlayerInventoryUtils.HELMET_SLOT
            EquipmentSlot.CHEST -> PlayerInventoryUtils.CHESTPLATE_SLOT
            EquipmentSlot.LEGS -> PlayerInventoryUtils.LEGGINGS_SLOT
            EquipmentSlot.FEET -> PlayerInventoryUtils.BOOTS_SLOT
            EquipmentSlot.BODY -> PlayerInventoryUtils.CHESTPLATE_SLOT
        }
    }

    fun getEquipmentSlot(slot: Int, heldSlot: Int): EquipmentSlot? {
        return when (slot) {
            PlayerInventoryUtils.OFFHAND_SLOT -> EquipmentSlot.OFF_HAND
            PlayerInventoryUtils.HELMET_SLOT -> EquipmentSlot.HEAD
            PlayerInventoryUtils.CHESTPLATE_SLOT -> EquipmentSlot.CHEST
            PlayerInventoryUtils.LEGGINGS_SLOT -> EquipmentSlot.LEGS
            PlayerInventoryUtils.BOOTS_SLOT -> EquipmentSlot.FEET
            else -> if (slot == heldSlot) EquipmentSlot.MAIN_HAND else null
        }
    }

    fun getEquipment(slot: EquipmentSlot, heldSlot: Int): ItemStack {
        if (slot == EquipmentSlot.CHEST) return ItemStack.AIR
        return slots[getSlotId(slot, heldSlot)] ?: ItemStack.AIR
    }

    fun setEquipment(slot: EquipmentSlot, heldSlot: Int, itemStack: ItemStack) {
        if (slot == EquipmentSlot.CHEST) throw IllegalArgumentException("PlayerInventory does not support chest slot")
        slots[getSlotId(slot, heldSlot)] = itemStack
    }

    override fun set(slot: Int, item: ItemStack) {
        var newItem = item
        val equipmentSlot = getEquipmentSlot(slot, player.heldSlot.value)
        if(equipmentSlot != null) {
            val event = PlayerEquipEvent(player, item, equipmentSlot)
            Events.dispatch(event)
            newItem = event.item
        }

        super.set(slot, newItem)
    }

    init {
        cursorItem.valueChanged {
            if(cursorItem.value == it.newValue) return@valueChanged
            player.sendPacket(ClientboundSetInventoryCursorPacket(it.newValue))
        }

        slots.itemSet {
            sendInventoryUpdate(it.key)
            if (it.key == player.heldSlot.value) {
                entity.equipment.value = entity.equipment.value.apply { mainHand = it.value }
            }
        }

        slots.itemRemoved {
            sendInventoryUpdate(it.key)
            if (it.key == player.heldSlot.value) {
                entity.equipment.value = entity.equipment.value.apply { mainHand = it.value }
            }
        }
    }

    override fun sendInventoryUpdate(slot: Int) {

        val equipmentSlot = getEquipmentSlot(slot, player.heldSlot.value)
        if(equipmentSlot != null) {
            //TODO redo entity equipment
        }
        player.sendPacket(ClientboundSetInventorySlotPacket(slot, slots[slot] ?: ItemStack.AIR))
    }

    fun sendFullInventoryUpdate() {
    }

    override fun drop(itemStack: ItemStack, isEntireStack: Boolean, isHeld: Boolean) {
        val player = entity as Player

        val event = PlayerDropItemEvent(player, itemStack)
        Events.dispatch(event)
        if (event.cancelled) {
            sendFullInventoryUpdate()
            return
        }

        if (isHeld) {
            val held = player.getHeldItem(PlayerHand.MAIN_HAND)
            val newItem = if (isEntireStack) {
                if (held.amount == 1) ItemStack.AIR else held.clone().apply { amount -= 1 }
            } else {
                ItemStack.AIR
            }
            player.inventory[player.heldSlot.value] = newItem
        }
    }
}

fun Player.give(itemStack: ItemStack) {
    this.inventory.give(itemStack)
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