package io.github.dockyardmc.ui.components

import io.github.dockyardmc.ui.CompositeDrawable

class FillFlowContainer(val direction: Direction, val components: List<CompositeDrawable>) : CompositeDrawable() {

    enum class Direction {
        LEFT,
        RIGHT,
        UP,
        DOWN
    }

    override fun buildComponent() {
        var x = 0
        var y = 0

        components.forEach { drawable ->
            withComposite(x, y, drawable)
            when (direction) {
                Direction.LEFT -> x--
                Direction.RIGHT -> x++
                Direction.UP -> y--
                Direction.DOWN -> y++
            }
        }
    }

    override fun dispose() {
        components.forEach { drawable -> drawable.dispose() }
    }

}