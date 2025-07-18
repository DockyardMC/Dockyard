package io.github.dockyardmc.protocol.types

object ARGB {

    fun alpha(int: Int): Int {
        return int ushr 24
    }

    fun red(int: Int): Int {
        return int shr 16 and 0xFF
    }

    fun green(int: Int): Int {
        return int shr 8 and 0xFF
    }

    fun blue(int: Int): Int {
        return int and 0xFF
    }

    fun color(a: Int, r: Int, g: Int, b: Int): Int {
        return a shl 24 or (r shl 16) or (g shl 8) or b
    }

    fun color(r: Int, g: Int, b: Int): Int {
        return color(255, r, g, b)
    }

}