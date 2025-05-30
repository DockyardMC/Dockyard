package io.github.dockyardmc.ui

import cz.lukynka.bindables.BindableList
import io.github.dockyardmc.maths.randomInt
import io.github.dockyardmc.maths.vectors.Vector2
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.registries.ItemRegistry
import io.github.dockyardmc.ui.components.ScrollableContainer

class TestScreen : Screen() {

    override val rows: Int = 6
    val items = BindableList<DrawableItemStack>()
    val scrollableContainer = ScrollableContainer(ScrollableContainer.Layout.VERTICAL, Vector2(7, 2), false, items)

    override fun buildComponent() {
        repeat(15) {
            items.add(DrawableItemStack(ItemRegistry.items.values.random().toItemStack(randomInt(1, 64))))
        }

        withSlot(7, 5) {
            withItem(Items.SPECTRAL_ARROW)
            onClick { _, clickType ->
                if (clickType == DrawableItemStack.ClickType.LEFT_CLICK) {
                    scrollableContainer.scrollNext()
                } else {
                    scrollableContainer.scrollPrevious()
                }
            }
        }

        withComposite(1, 1, scrollableContainer)
    }
}