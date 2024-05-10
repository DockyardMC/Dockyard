package io.github.dockyardmc.player

import io.github.dockyardmc.extentions.properStrictCase

enum class GameMode {
    SURVIVAL,
    CREATIVE,
    ADVENTURE,
    SPECTATOR;

    override fun toString(): String {
        return this.name.properStrictCase()
    }
}