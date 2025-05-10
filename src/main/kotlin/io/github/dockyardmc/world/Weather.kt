package io.github.dockyardmc.world

enum class Weather(val rain: Boolean, val thunder: Boolean) {
    CLEAR(false, false),
    RAIN(true, false),
    THUNDER(true, true)
}