package io.github.dockyardmc.ui

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.registries.Item

class DrawableItemStack() {

    var itemStack: ItemStack = ItemStack.air
    var clickListener: ((Player, ClickType) -> Unit)? = null

    fun withItem(itemStack: ItemStack) {
        this.itemStack = itemStack
    }

    fun withItem(item: Item, amount: Int = 1) {
        this.itemStack = item.toItemStack(amount)
    }

    fun onClick(unit: (Player, ClickType) -> Unit) {
        this.clickListener = unit
    }
}

fun Item.toDrawable(): DrawableItemStack {
    return drawableItemStack { withItem(this@toDrawable) }
}

fun ItemStack.toDrawable(): DrawableItemStack {
    return drawableItemStack { withItem(this@toDrawable) }
}


enum class ClickType {
    NORMAL,
    SHIFT_NORMAL
}

fun drawableItemStack(unit: DrawableItemStack.() -> Unit): DrawableItemStack {
    val item = DrawableItemStack()
    unit.invoke(item)
    return item
}