package io.github.dockyardmc.extentions

import io.github.dockyardmc.scroll.CustomColor

//TODO add to Scroll
fun CustomColor.toRgbInt(): Int {
    var r = this.r
    var g = this.g
    var b = this.b

    r = (r shl 16) and 0x00FF0000
    g = (g shl 8) and 0x0000FF00
    b = b and 0x000000FF

    return -0x1000000 or r or g or b
}

fun CustomColor.Companion.fromRGBInt(color: Int): CustomColor {
    val red = (color shr 16) and 0xFF
    val green = (color shr 8) and 0xFF
    val blue = color and 0xFF
    return CustomColor(red, green, blue)
}