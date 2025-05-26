package io.github.dockyardmc.ui.new.components

import io.github.dockyardmc.ui.new.CompositeDrawable
import io.github.dockyardmc.ui.new.DrawableItem

class StaticDrawableItemComponent(val item: DrawableItem) : CompositeDrawable() {

    override fun buildComponent() {
        withSlot(0, item)
    }

    override fun dispose() {}
}