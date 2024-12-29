package io.github.dockyardmc.ui

import io.github.dockyardmc.item.ItemComponent
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.registries.Item

class DrawableItemStack {

    var itemStack: ItemStack = ItemStack.AIR
    var clickListener: ((Player, DrawableClickType) -> Unit)? = null

    fun withItem(itemStack: ItemStack) {
        this.itemStack = itemStack
    }

    fun withItem(item: Item, amount: Int = 1) {
        this.itemStack = item.toItemStack(amount)
    }

    fun withName(name: String) {
        itemStack.withDisplayName(name)
    }

    fun addLoreLine(lore: String) {
        val mut = itemStack.existingMeta?.lore?.toMutableList() ?: mutableListOf()
        mut.add(lore)
        itemStack.withLore(mut)
    }

    fun withComponent(component: ItemComponent) {
        itemStack.withComponent(component)
    }

    fun onClick(unit: (Player, DrawableClickType) -> Unit) {
        this.clickListener = unit
    }
}

fun Item.toDrawable(): DrawableItemStack {
    return drawableItemStack { withItem(this@toDrawable) }
}

fun ItemStack.toDrawable(): DrawableItemStack {
    return drawableItemStack { withItem(this@toDrawable) }
}

fun drawableItemStack(unit: DrawableItemStack.() -> Unit): DrawableItemStack {
    val item = DrawableItemStack()
    unit.invoke(item)
    return item
}

enum class DrawableClickType {
    LEFT_CLICK,
    RIGHT_CLICK,
    LEFT_CLICK_SHIFT,
    RIGHT_CLICK_SHIFT,
    MIDDLE_CLICK,
    HOTKEY,
    OFFHAND,
    DROP,
    LEFT_CLICK_OUTSIDE_INVENTORY,
    RIGHT_CLICK_OUTSIDE_INVENTORY
}