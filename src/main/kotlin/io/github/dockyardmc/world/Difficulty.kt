package io.github.dockyardmc.world

import io.github.dockyardmc.extentions.properStrictCase

enum class Difficulty {
    PEACEFUL,
    EASY,
    NORMAL,
    HARD;

    override fun toString(): String = this.name.properStrictCase()
}

