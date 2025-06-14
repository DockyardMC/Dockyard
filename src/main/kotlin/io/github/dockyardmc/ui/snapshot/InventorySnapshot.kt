package io.github.dockyardmc.ui.snapshot

import cz.lukynka.prettylog.log
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.profiler.profiler
import io.github.dockyardmc.protocol.types.EquipmentSlot
import io.github.dockyardmc.utils.Disposable
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class InventorySnapshot(val player: Player) : Iterable<Pair<Int, ItemStack>>, Disposable {
    val created: Instant = Clock.System.now()

    private val indexToItemMap: MutableMap<Int, ItemStack> = mutableMapOf()
    private val equipment: MutableMap<EquipmentSlot, ItemStack> = mutableMapOf()

    val slots get() = indexToItemMap.toMap()
    val size get() = indexToItemMap.size

    fun restore() {
        equipment.forEach { (slot, item) ->
            player.equipment[slot] = item
        }
        indexToItemMap.forEach { (index, item) ->
            player.inventory[index] = item
        }
    }

    fun restoreAndDispose() {
        restore()
        dispose()
    }

    init {
        profiler("Take $player Inventory Snapshot") {

            // fill inventory with air
            repeat(46) { i ->
                indexToItemMap[i] = ItemStack.AIR
            }

            // fill equipment with air
            EquipmentSlot.entries.forEach { entry ->
                equipment[entry] = ItemStack.AIR
            }

            player.inventory.slots.values.forEach { (index, item) ->
                indexToItemMap[index] = item
            }

            player.equipment.values.forEach { (slot, item) ->
                equipment[slot] = item
            }
        }
    }

    override fun dispose() {
        indexToItemMap.clear()
        equipment.clear()
    }

    override fun toString(): String {
        return "InventorySnapshot(player: $player, created: ${created.toEpochMilliseconds()}, slots: $size)"
    }

    override fun iterator(): Iterator<Pair<Int, ItemStack>> {
        return slots.toList().iterator()
    }
}