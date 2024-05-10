package io.github.dockyardmc.world

import io.github.dockyardmc.extentions.properStrictCase
import java.util.*

enum class Difficulty {
    PEACEFUL,
    EASY,
    NORMAL,
    HARD;

    override fun toString(): String {
        return this.name.properStrictCase()
    }
}

