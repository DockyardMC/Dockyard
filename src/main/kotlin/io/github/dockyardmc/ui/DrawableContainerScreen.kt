package io.github.dockyardmc.ui

import io.github.dockyardmc.bindables.BindablePairMap
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.inventory.ContainerInventory
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.utils.Disposable

abstract class DrawableContainerScreen: ContainerInventory, Disposable {
    override var name: String = "Drawable Container"
    override var rows: Int = 1
    override var contents: MutableMap<Int, ItemStack> = mutableMapOf()

    open fun onClose(player: Player) { }
    open fun onOpen(player: Player) { }

    var slots = BindablePairMap<Int, DrawableItemStack>()
    val eventPool = EventPool()

    override fun dispose() {

    }
}

