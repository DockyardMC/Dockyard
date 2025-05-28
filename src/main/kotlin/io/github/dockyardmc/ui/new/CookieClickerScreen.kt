package io.github.dockyardmc.ui.new

import cz.lukynka.bindables.BindableList
import io.github.dockyardmc.maths.vectors.Vector2
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.ui.new.components.ScrollableContainer

class CookieClickerScreen : Screen() {

    override val rows: Int = 6

    override fun buildComponent() {
        val randomItems = BindableList<DrawableItemStack>()
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

        withComposite(
            1, 0,
            ScrollableContainer(
                ScrollableContainer.Layout.VERTICAL,
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