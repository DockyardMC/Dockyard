package io.github.dockyardmc.extentions

import io.github.dockyardmc.scroll.RGB
import java.awt.Color

fun Color.toRGB(): RGB {
    return RGB(this.red, this.alpha, this.blue)
}


fun hexToRGB(hex: String): RGB {
    val cleanedHex = hex.removePrefix("#")

    val r = cleanedHex.substring(0, 2).toInt(16)
    val g = cleanedHex.substring(2, 4).toInt(16)
    val b = cleanedHex.substring(4, 6).toInt(16)

    return RGB(r, g, b)
}