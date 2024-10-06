package io.github.dockyardmc.ui

import io.github.dockyardmc.bindables.BindablePairMap
import io.github.dockyardmc.bindables.PairKey
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.inventory.ContainerInventory
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundSetInventorySlotPacket
import io.github.dockyardmc.utils.Disposable
import io.github.dockyardmc.utils.vectors.Vector2

abstract class DrawableContainerScreen(val player: Player): ContainerInventory, Disposable {
    override var name: String = "Drawable Container"
    override var rows: Int = 1
    override var contents: MutableMap<Int, ItemStack> = mutableMapOf()

    open fun onClose(player: Player) { }
    open fun onOpen(player: Player) { }

    var slots = BindablePairMap<Int, DrawableItemStack>()
    val eventPool = EventPool()

    init {
        slots.itemSet {
            val slot = getSlotIndexFromVector2(it.first, it.second)
            contents[slot] = it.value.itemStack
            player.sendPacket(ClientboundSetInventorySlotPacket(1, 0, slot, it.value.itemStack))
        }

        slots.itemRemoved {
            val slot = getSlotIndexFromVector2(it.first, it.second)
            contents[slot] = ItemStack.air
            player.sendPacket(ClientboundSetInventorySlotPacket(1, 0, slot, ItemStack.air))
        }
    }

    fun fill(from: Vector2, to: Vector2, item: DrawableItemStack) {
        for (x in from.x..to.x) {
            for (y in from.y..to.y) {
                slots[x, y] = item
            }
        }
    }

    fun fill(from: Pair<Int, Int>, to: Pair<Int, Int>, item: DrawableItemStack) {
        for (x in from.first..to.first) {
            for (y in from.second..to.second) {
                slots[x, y] = item
            }
        }
    }

    fun click(slot: Int, player: Player, clickType: DrawableClickType) {
        val vec2 = getVector2FromSlotIndex(slot)
        val drawableItem = slots[PairKey(vec2.x, vec2.y)] ?: ItemStack.air.toDrawable()

        slots[vec2.x, vec2.y] = drawableItem
        player.inventory.carriedItem.value = ItemStack.air

        drawableItem.clickListener?.invoke(player, clickType)
    }

    override fun dispose() {
        eventPool.dispose()
    }
}

fun getSlotIndexFromVector2(x: Int, y: Int): Int {
    require(!(x < 0 || y < 0)) { "Coordinates cannot be negative" }
    return x + (y * 9)
}

fun getVector2FromSlotIndex(slotIndex: Int): Vector2 {
    require(slotIndex >= 0) { "Slot index cannot be negative" }

    val y = slotIndex / 9
    val x = slotIndex % 9

    return Vector2(x, y)
}