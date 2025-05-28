package io.github.dockyardmc.ui.new

import cz.lukynka.bindables.BindableList
import io.github.dockyardmc.maths.vectors.Vector2
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.ui.new.components.ScrollableContainer

class CookieClickerScreen : Screen() {

    override val rows: Int = 6

    override fun buildComponent() {
        val randomItems = BindableList<DrawableItemStack>()
        val scrollableContainer = ScrollableContainer(ScrollableContainer.Layout.VERTICAL, Vector2(7, 4), true, randomItems)

        // for testing stuffs
        repeat(37) {
            val item = drawableItemStack {
                withItem(Items.AMETHYST_CLUSTER)
                withAmount(it + 1)
                withNoxesiumImmovable(true)
                onClick { _, _ ->
                    randomItems.remove(randomItems.values.random())
                }
            }
            randomItems.add(item)
        }

        withSlot(8, 4) {
            withItem(Items.SPECTRAL_ARROW)
            withName("<lime><u>Scroll")
            onClick { _, clickType ->
                when (clickType) {
                    DrawableItemStack.ClickType.LEFT_CLICK -> scrollableContainer.scrollNext()
                    DrawableItemStack.ClickType.RIGHT_CLICK -> scrollableContainer.scrollPrevious()
                    DrawableItemStack.ClickType.MIDDLE_CLICK -> scrollableContainer.resetScrollPosition()
                    else -> { /* fuck off */ }
                }
            }
        }

        withComposite(1, 0, scrollableContainer)
    }
}