package io.github.dockyardmc.ui.new

import io.github.dockyardmc.maths.randomInt
import io.github.dockyardmc.maths.vectors.Vector2
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.registries.ItemRegistry
import io.github.dockyardmc.ui.new.components.ScrollableContainer

class CookieClickerScreen : Screen() {

    override val rows: Int = 6

    override fun buildComponent() {
        val randomItems = mutableListOf<DrawableItemStack>()
        repeat(65) {
            randomItems.add(DrawableItemStack(ItemRegistry.items.values.random().toItemStack(randomInt(1, 64))))
        }

        withComposite(
            1, 0,
            ScrollableContainer(
                ScrollableContainer.Direction.HORIZONTAL,
                ScrollableContainer.Direction.HORIZONTAL,
                Vector2(7, 4),
                true,
                Items.LIME_STAINED_GLASS_PANE.toItemStack().withDisplayName("Next"),
                Items.LIME_STAINED_GLASS_PANE.toItemStack().withDisplayName("Prev"),
                true,
                randomItems,
            )
        )
    }
}