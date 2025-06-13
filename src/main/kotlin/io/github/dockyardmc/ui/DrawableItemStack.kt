package io.github.dockyardmc.ui

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.registries.Item

class DrawableItemStack(val itemStack: ItemStack, val onClick: ((Player, ClickType) -> Unit)? = null) {

    enum class ClickType {
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

    class Builder {
        private var itemStack: ItemStack = ItemStack.AIR
        private var onClick: ((Player, ClickType) -> Unit)? = null
        private var noxesiumImmovable: Boolean = true

        fun withItemStack(itemStack: ItemStack) {
            this.itemStack = itemStack
        }

        fun withItem(item: Item) {
            withItemStack(item.toItemStack())
        }

        fun withName(name: String) {
            itemStack = itemStack.withDisplayName(name)
        }

        fun withLore(vararg lore: String) {
            itemStack = itemStack.withLore(*lore)
        }

        fun withAmount(amount: Int) {
            itemStack = itemStack.withAmount(amount)
        }

        fun onClick(onClick: (Player, ClickType) -> Unit) {
            this.onClick = onClick
        }

        fun withNoxesiumImmovable(immovable: Boolean) {
            this.noxesiumImmovable = immovable
        }

        fun build(): DrawableItemStack {
            return DrawableItemStack(itemStack.withNoxesiumImmovable(noxesiumImmovable), onClick)
        }
    }
}

fun drawableItemStack(unit: DrawableItemStack.Builder.() -> Unit): DrawableItemStack {
    val builder = DrawableItemStack.Builder()
    unit.invoke(builder)
    return builder.build()
}