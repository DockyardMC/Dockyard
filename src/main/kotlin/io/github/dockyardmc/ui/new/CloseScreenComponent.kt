package io.github.dockyardmc.ui.new

import io.github.dockyardmc.registry.Items

class CloseScreenComponent: CompositeDrawable() {

    override fun buildComponent() {
        withSlot(0) {
            withItem(Items.BARRIER)
            withName("<red>Close Screen")
            onClick { player, _ ->
                player.closeInventory()
            }
        }
    }

    override fun dispose() {
        super.bindablePool
    }
}