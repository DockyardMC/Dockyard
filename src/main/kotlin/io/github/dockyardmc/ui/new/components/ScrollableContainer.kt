package io.github.dockyardmc.ui.new.components

import cz.lukynka.bindables.BindableList
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.maths.vectors.Vector2
import io.github.dockyardmc.ui.new.CompositeDrawable
import io.github.dockyardmc.ui.new.DrawableItemStack
import io.github.dockyardmc.ui.new.drawableItemStack

open class ScrollableContainer(val layout: Layout, val size: Vector2, val smoothScrolling: Boolean, val arrowNext: ItemStack, val arrowPrevious: ItemStack, val largeArrows: Boolean, val items: BindableList<DrawableItemStack>) : CompositeDrawable() {

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

        if (layout == Layout.HORIZONTAL) {
            val arrowPrevLoc = Vector2(-1, 0)
            val arrowNextLoc = Vector2(size.x, 0)

            withSlot(arrowNextLoc.x, arrowNextLoc.y, getNextArrow())
            withSlot(arrowPrevLoc.x, arrowPrevLoc.y, getPrevArrow())

            if (largeArrows) {
                for (i in 0 until (size.y)) {
                    withSlot(arrowNextLoc.x, arrowNextLoc.y + i, getNextArrow())
                }
                for (i in 0 until (size.y)) {
                    withSlot(arrowPrevLoc.x, arrowPrevLoc.y + i, getPrevArrow())
                }
            }
        } else {
            val arrowPrevLoc = Vector2(size.x, 0)
            val arrowNextLoc = Vector2(size.x, size.y - 1)

            withSlot(arrowNextLoc.x, arrowNextLoc.y, getNextArrow())
            withSlot(arrowPrevLoc.x, arrowPrevLoc.y, getPrevArrow())
        }
        fillVisibleSlots()
    }

    private fun getNextArrow(): DrawableItemStack {
        return drawableItemStack {
            withItemStack(arrowNext)
            withNoxesiumImmovable(true)
            onClick { _, _ ->
                scrollNext()
            }
        }
    }

    private fun getPrevArrow(): DrawableItemStack {
        return drawableItemStack {
            withItemStack(arrowPrevious)
            withNoxesiumImmovable(true)
            onClick { _, _ ->
                scrollPrevious()
            }
        }
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


    fun scrollNext() {
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
        }
    }


    fun scrollPrevious() {
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
        }
    }


    private fun rebuildItems() {
        currentOffset = currentOffset.coerceAtLeast(0)
        val maxOffset = items.size - (if (layout == Layout.HORIZONTAL) rowSize else totalSlots)
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