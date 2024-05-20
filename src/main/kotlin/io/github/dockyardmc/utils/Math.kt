package io.github.dockyardmc.utils

import java.lang.Math

object Math {

    fun positiveCeilDiv(i: Int, j: Int): Int {
        return -Math.floorDiv(-i, j)
    }
}