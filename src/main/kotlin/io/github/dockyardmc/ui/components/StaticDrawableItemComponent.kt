package io.github.dockyardmc.ui.components

import io.github.dockyardmc.ui.CompositeDrawable
import io.github.dockyardmc.ui.DrawableItemStack

class StaticDrawableItemComponent(val item: DrawableItemStack) : CompositeDrawable() {

    override fun buildComponent() {
        withSlot(0, item)
    }

    override fun dispose() {}
}