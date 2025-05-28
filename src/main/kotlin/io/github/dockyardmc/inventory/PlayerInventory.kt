package io.github.dockyardmc.inventory

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.config.ConfigManager
import io.github.dockyardmc.entity.EntityManager.spawnEntity
import io.github.dockyardmc.entity.ItemDropEntity
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerDropItemEvent
import io.github.dockyardmc.events.PlayerEquipEvent
import io.github.dockyardmc.events.PlayerSwapOffhandEvent
import io.github.dockyardmc.item.EquipmentSlot
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.item.isSameAs
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundSetInventoryCursorPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundSetInventorySlotPacket
import io.github.dockyardmc.registry.registries.Item
import io.github.dockyardmc.utils.getPlayerEventContext

class PlayerInventory(var player: Player) : EntityInventory(player, INVENTORY_SIZE) {
    val name: String = "Inventory"

    companion object {
        const val INVENTORY_SIZE: Int = 46
        const val INNER_INVENTORY_SIZE: Int = 36
    }

    val cursorItem: Bindable<ItemStack> = Bindable(ItemStack.AIR)

    override fun getWindowId(): Byte {
        return 0
    }

    override fun clear() {
        cursorItem.value = ItemStack.AIR
        super.clear()

        player.equipment.clear()
    }

    fun getSlotId(slot: EquipmentSlot, heldSlot: Int): Int {
        return when (slot) {
            EquipmentSlot.MAIN_HAND -> heldSlot
            EquipmentSlot.OFF_HAND -> PlayerInventoryUtils.OFFHAND_SLOT
            EquipmentSlot.HELMET -> PlayerInventoryUtils.HELMET_SLOT
            EquipmentSlot.CHESTPLATE -> PlayerInventoryUtils.CHESTPLATE_SLOT
            EquipmentSlot.LEGGINGS -> PlayerInventoryUtils.LEGGINGS_SLOT
            EquipmentSlot.BOOTS -> PlayerInventoryUtils.BOOTS_SLOT
            EquipmentSlot.BODY -> PlayerInventoryUtils.CHESTPLATE_SLOT
        }
    }

    fun getEquipmentSlot(slot: Int, heldSlot: Int): EquipmentSlot? {
        return when (slot) {
            PlayerInventoryUtils.OFFHAND_SLOT -> EquipmentSlot.OFF_HAND
            PlayerInventoryUtils.HELMET_SLOT -> EquipmentSlot.HELMET
            PlayerInventoryUtils.CHESTPLATE_SLOT -> EquipmentSlot.CHESTPLATE
            PlayerInventoryUtils.LEGGINGS_SLOT -> EquipmentSlot.LEGGINGS
            PlayerInventoryUtils.BOOTS_SLOT -> EquipmentSlot.BOOTS
            else -> if (slot == heldSlot) EquipmentSlot.MAIN_HAND else null
        }
    }

    fun unsafeUpdateEquipmentSlot(slot: EquipmentSlot, heldSlot: Int, itemStack: ItemStack) {
        slots.setSilently(getSlotId(slot, heldSlot), itemStack)
        player.equipment.setSilently(slot, itemStack)
    }

    fun swapOffhand() {
        val offhandItem = player.offHandItem
        val mainHandItem = player.mainHandItem

        val event = PlayerSwapOffhandEvent(player, mainHandItem, offhandItem, getPlayerEventContext(player))
        Events.dispatch(event)
        if (event.cancelled) return

        player.mainHandItem = event.offHandItem
        player.offHandItem = event.mainHandItem
    }

    fun getSlotByItemStack(itemStack: ItemStack): Int? {
        slots.values.forEach { (index, item) ->
            if (item.isSameAs(itemStack)) return index
        }
        return null
    }

    fun getSlotByItem(item: Item): Int? {
        slots.values.forEach { (index, itemStack) ->
            if (itemStack.material == item) {
                return index
            }
        }

        return null
    }

    override fun set(slot: Int, item: ItemStack) {
        var newItem = item
        val equipmentSlot = getEquipmentSlot(slot, player.heldSlotIndex.value)
        if (equipmentSlot != null) {
            val event = PlayerEquipEvent(player, item, equipmentSlot, getPlayerEventContext(player))
            newItem = event.item
            Events.dispatch(event)
        }

        super.set(slot, newItem)
    }

    init {
        cursorItem.valueChanged {
            player.sendPacket(ClientboundSetInventoryCursorPacket(it.newValue))
        }

        slots.itemSet {
            sendInventoryUpdate(it.key)
            if (it.key == player.heldSlotIndex.value) {
                entity.equipment[EquipmentSlot.MAIN_HAND] = it.value
            }
        }

        slots.itemRemoved {
            sendInventoryUpdate(it.key)
            if (it.key == player.heldSlotIndex.value) {
                entity.equipment[EquipmentSlot.MAIN_HAND] = it.value
            }
        }
    }

    override fun sendInventoryUpdate(slot: Int) {

        val equipmentSlot = getEquipmentSlot(slot, player.heldSlotIndex.value)
        if (equipmentSlot != null) player.equipment.triggerUpdate()
        player.sendPacket(ClientboundSetInventorySlotPacket(slot, slots[slot] ?: ItemStack.AIR))
    }

    fun sendFullInventoryUpdate() {
        for (i in 0 until INNER_INVENTORY_SIZE) {
            sendInventoryUpdate(i)
        }
        player.inventory.cursorItem.triggerUpdate()
    }

    fun drop(itemStack: ItemStack): Boolean {
        val player = entity as Player

        val event = PlayerDropItemEvent(player, itemStack)
        Events.dispatch(event)

        if (itemStack.noxesiumImmovable) return true

        if (event.cancelled) {
            sendFullInventoryUpdate()
            return true
        }

        if (ConfigManager.config.implementationConfig.itemDroppingAndPickup) {
            player.world.spawnEntity(ItemDropEntity(player.location, itemStack))
        }
        return false
    }
}

fun Player.give(itemStack: ItemStack): Boolean {
    return this.inventory.give(itemStack)
}

fun Player.give(vararg itemStack: ItemStack) {
    itemStack.forEach(::give)
}

fun Player.give(vararg items: Item) {
    items.forEach { item ->
        give(item.toItemStack())
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