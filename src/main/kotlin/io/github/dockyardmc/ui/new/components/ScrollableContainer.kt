package io.github.dockyardmc.ui.new.components

import cz.lukynka.bindables.BindableList
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.maths.vectors.Vector2
import io.github.dockyardmc.ui.new.CompositeDrawable
import io.github.dockyardmc.ui.new.DrawableItemStack
import io.github.dockyardmc.ui.new.drawableItemStack

open class ScrollableContainer(val layout: Direction, val scrollDirection: Direction, val size: Vector2, val smoothScrolling: Boolean, val arrowNext: ItemStack, val arrowPrevious: ItemStack, val largeArrows: Boolean, val items: BindableList<DrawableItemStack>) : CompositeDrawable() {

    enum class Direction {
        VERTICAL,
        HORIZONTAL
    }

    private var currentOffset = 0
    private val totalSlots = size.x * size.y
    private val rowSize = size.x

    fun canScrollNext(): Boolean {
        return when (scrollDirection) {
            Direction.HORIZONTAL -> {
                val lastVisibleIndex = currentOffset + (size.y - 1) * (items.size / size.y) + size.x - 1
                lastVisibleIndex < items.size - 1
            }

            Direction.VERTICAL -> (currentOffset + totalSlots) < items.size
        }
    }

    fun canScrollPrevious(): Boolean {
        return currentOffset > 0
    }

    override fun buildComponent() {

        items.listUpdated {
            currentOffset = 0
            rebuildItems()
        }

        if (layout == Direction.HORIZONTAL) {
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
                val index = when (scrollDirection) {
                    Direction.HORIZONTAL -> {
                        val rowOffset = y * size.x
                        currentOffset + rowOffset + x
                    }
                    Direction.VERTICAL -> currentOffset + (y * size.x) + x
                }
                if (index < items.size) {
                    withSlot(x, y, items.values[index])
                } else {
                    withSlot(x, y, DrawableItemStack(ItemStack.AIR))
                }
            }
        }
    }



    fun scrollNext() {
        if (canScrollNext()) {
            currentOffset += when (scrollDirection) {
                Direction.HORIZONTAL -> {
                    if (smoothScrolling) 1 else rowSize
                }

                Direction.VERTICAL -> {
                    if (smoothScrolling) rowSize else totalSlots
                }
            }
            rebuildItems()
        }
    }

    fun scrollPrevious() {
        if (canScrollPrevious()) {
            currentOffset -= when (scrollDirection) {
                Direction.HORIZONTAL -> {
                    if (smoothScrolling) 1 else rowSize
                }

                Direction.VERTICAL -> {
                    if (smoothScrolling) rowSize else totalSlots
                }
            }
            rebuildItems()
        }
    }


    private fun rebuildItems() {
        currentOffset = currentOffset.coerceAtLeast(0)
        val maxOffset = items.size - (if (scrollDirection == Direction.HORIZONTAL) rowSize else totalSlots)
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
    }
}