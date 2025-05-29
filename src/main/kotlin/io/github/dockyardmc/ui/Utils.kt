package io.github.dockyardmc.ui

import io.github.dockyardmc.maths.vectors.Vector2

fun getSlotIndexFromVector2(x: Int, y: Int): Int {
    return x + (y * 9)
}

fun getVector2FromSlotIndex(slotIndex: Int): Vector2 {
    val y = slotIndex / 9
    val x = slotIndex % 9

    return Vector2(x, y)
}