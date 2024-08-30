package io.github.dockyardmc.inventory

import cz.lukynka.Bindable
import cz.lukynka.BindableMap
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.entities.EntityManager.spawnEntity
import io.github.dockyardmc.entities.ItemDropEntity
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundSetInventorySlotPacket
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.utils.MathUtils

class Inventory(var entity: Entity) {
    val name: String = "Inventory"
    val size = entity.inventorySize
    val slots: BindableMap<Int, ItemStack> = BindableMap()
    var carriedItem: Bindable<ItemStack> = Bindable(ItemStack.air)

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
        carriedItem.valueChanged { sendInventoryUpdate(-1) }
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

    //TODO make work
    fun Player.give(itemStack: ItemStack) {

    }

    fun Player.clearInventory() {
        this@Inventory.clear()
    }

    fun sendInventoryUpdate(slot: Int) {
        val player = entity as Player
        val clientSlot =  MathUtils.toOriginalSlotIndex(slot)
        val item = if(slot == -1) carriedItem.value else slots[slot]
        val windowId = if(slot == -1) -1 else 0
        val packet = ClientboundSetInventorySlotPacket(windowId, 0, clientSlot, item ?: ItemStack.air)
        player.sendPacket(packet)
    }

    fun sendFullInventoryUpdate() {
        sendInventoryUpdate(-1)
        repeat(size) {
            sendInventoryUpdate(it)
        }
        sendInventoryUpdate(size +1)
        sendInventoryUpdate(size)
    }

    fun drop(itemStack: ItemStack) {
        val loc = entity.location
        val drop = ItemDropEntity(loc)
        loc.world.spawnEntity(drop)
    }
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