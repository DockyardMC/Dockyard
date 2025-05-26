package io.github.dockyardmc.ui.new

import io.github.dockyardmc.maths.vectors.Vector2

fun getSlotIndexFromVector2(x: Int, y: Int): Int {
    if (x < 0 || y < 0) throw Screen.InvalidScreenSlotOperationException("Slot coordinates can't be negative")
    return x + (y * 9)
}

fun getVector2FromSlotIndex(slotIndex: Int): Vector2 {
    if (slotIndex < 0) throw Screen.InvalidScreenSlotOperationException("Slot index can't be negative!")

    val y = slotIndex / 9
    val x = slotIndex % 9

    return Vector2(x, y)
}