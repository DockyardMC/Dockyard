package io.github.dockyardmc.ui.new

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.registries.Item
import io.github.dockyardmc.ui.DrawableClickType

class DrawableItem(val itemStack: ItemStack, val onClick: ((Player, DrawableClickType) -> Unit)? = null) {

    class Builder {
        private var itemStack: ItemStack = ItemStack.AIR
        private var onClick: ((Player, DrawableClickType) -> Unit)? = null
        private var noxesiumImmovable: Boolean? = null

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

        fun onClick(onClick: (Player, DrawableClickType) -> Unit) {
            this.onClick = onClick
        }

        fun isNoxesiumImmovable(immovable: Boolean) {
            this.noxesiumImmovable = immovable
        }

        fun build(): DrawableItem {
            if (noxesiumImmovable != null) itemStack.withNoxesiumImmovable(noxesiumImmovable!!)
            return DrawableItem(itemStack, onClick)
        }
    }
}