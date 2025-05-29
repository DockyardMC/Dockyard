package io.github.dockyardmc.ui.components

import cz.lukynka.bindables.BindableList
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.maths.vectors.Vector2
import io.github.dockyardmc.ui.CompositeDrawable
import io.github.dockyardmc.ui.DrawableItemStack

open class ScrollableContainer(
    val layout: Layout,
    val size: Vector2,
    val smoothScrolling: Boolean,
    private val items: BindableList<DrawableItemStack>
) : CompositeDrawable() {

    enum class Layout {
        VERTICAL,
        HORIZONTAL
    }

    private var currentOffset = 0
    private val totalSlots = size.x * size.y
    private val rowSize = size.x

    fun canScrollNext(): Boolean {
        return when (layout) {
            Layout.HORIZONTAL -> {
                val visibleColumns = if (smoothScrolling) 1 else size.x
                (currentOffset + visibleColumns) * size.y < items.size
            }

            Layout.VERTICAL -> {
                if (smoothScrolling) {
                    (currentOffset + 1) * size.x < items.size
                } else {
                    (currentOffset + size.y) * size.x < items.size
                }
            }
        }
    }

    fun canScrollPrevious(): Boolean {
        return currentOffset > 0
    }

    private lateinit var itemsBindableListener: BindableList.BindableListUpdateListener<DrawableItemStack>

    override fun buildComponent() {

        itemsBindableListener = items.listUpdated {
            currentOffset = 0
            rebuildItems()
        }

        fillVisibleSlots()
    }

    private fun fillVisibleSlots() {
        for (y in 0 until size.y) {
            for (x in 0 until size.x) {
                withSlot(x, y, DrawableItemStack(ItemStack.AIR))
            }
        }

        when (layout) {
            Layout.HORIZONTAL -> {
                for (y in 0 until size.y) {
                    for (x in 0 until size.x) {
                        val column = x + currentOffset
                        val index = column * size.y + y
                        if (index < items.size) {
                            withSlot(x, y, items.values[index])
                        }
                    }
                }
            }

            Layout.VERTICAL -> {
                val startIndex = currentOffset * size.x
                for (y in 0 until size.y) {
                    for (x in 0 until size.x) {
                        val index = startIndex + (y * size.x) + x
                        if (index < items.size) {
                            withSlot(x, y, items.values[index])
                        }
                    }
                }
            }
        }
    }

    fun scrollNext(): Boolean {
        if (canScrollNext()) {
            if (smoothScrolling) {
                currentOffset++
            } else {
                currentOffset += when (layout) {
                    Layout.HORIZONTAL -> size.x
                    Layout.VERTICAL -> size.y
                }
            }
            rebuildItems()
            return true
        }
        return false
    }


    fun scrollPrevious(): Boolean {
        if (canScrollPrevious()) {
            if (smoothScrolling) {
                currentOffset--
            } else {
                currentOffset -= when (layout) {
                    Layout.HORIZONTAL -> size.x
                    Layout.VERTICAL -> size.y
                }
            }
            rebuildItems()
            return true
        }
        return false
    }

    fun resetScrollPosition() {
        currentOffset = 0
        fillVisibleSlots()
    }

    private fun rebuildItems() {
        currentOffset = currentOffset.coerceAtLeast(0)
        val maxOffset = items.size - if (layout == Layout.HORIZONTAL) rowSize else totalSlots
        currentOffset = currentOffset.coerceAtMost(maxOffset.coerceAtLeast(0))

        clearSlots()
        fillVisibleSlots()
    }

    private fun clearSlots() {
        for (x in 0 until size.x) {
            for (y in 0 until size.y) {
                withSlot(x, y, DrawableItemStack(ItemStack.AIR))
            }
        }
    }

    override fun dispose() {
        items.unregister(itemsBindableListener)
    }
}