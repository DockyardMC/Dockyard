package io.github.dockyardmc.ui

import cz.lukynka.bindables.BindableList
import io.github.dockyardmc.maths.vectors.Vector2
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.ui.components.ScrollableContainer

class TestScreen : Screen() {

    val items = BindableList<DrawableItemStack>()
    val scrollableContainer = ScrollableContainer(ScrollableContainer.Layout.VERTICAL, Vector2(7, 2), false, items)

    override fun buildComponent() {
        withScreenName("test")
        withScreenFullscreen(true)
        withScreenRows(6)

        withSlot(4, 2) {
            withItem(Items.OMINOUS_TRIAL_KEY)
            withName("<#3a8567><u>Trial Key")
            withLore("", "<gray>Very ominous.")
        }
    }

    override fun dispose() {
        items.dispose()
        super.dispose()
    }
}