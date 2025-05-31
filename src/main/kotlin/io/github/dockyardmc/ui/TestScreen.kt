package io.github.dockyardmc.ui

import cz.lukynka.bindables.BindableList
import io.github.dockyardmc.maths.randomInt
import io.github.dockyardmc.maths.vectors.Vector2
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.registries.ItemRegistry
import io.github.dockyardmc.ui.components.ScrollableContainer

class TestScreen() : Screen() {

    val items = BindableList<DrawableItemStack>()
    val scrollableContainer = ScrollableContainer(ScrollableContainer.Layout.VERTICAL, Vector2(7, 2), false, items)

    override fun buildComponent() {
        withScreenName("testing testings")
        withScreenFullscreen(true)
        withScreenRows(5)
        
        items.clear()
        repeat(20) {
            items.add(DrawableItemStack(ItemRegistry.items.values.random().toItemStack(randomInt(1, 33))))
        }

        withSlot(5, 5) {
            withItem(Items.END_PORTAL_FRAME)
        }
        withSlot(5, 3) {
            withItem(Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE)
        }

        withSlot(7, 5) {
            withItem(Items.APPLE)
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