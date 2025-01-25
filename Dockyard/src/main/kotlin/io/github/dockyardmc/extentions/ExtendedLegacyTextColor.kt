package io.github.dockyardmc.extentions

fun getLegacyTextColorNameFromVanilla(name: String): String {
    return when(name) {
        "dark_aqua" -> "cyan"
        "dark_green" -> "green"
        "dark_purple" -> "purple"
        "gold" -> "orange"
        "green" -> "lime"
        "light_purple" -> "pink"
        "reset" -> "white"
        else -> name
    }
}