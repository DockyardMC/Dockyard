package io.github.dockyardmc.scroll

//Ordered in precise way cause text colors used to be enum in the minecraft client. It still uses that in some packets
//https://wiki.vg/Text_formatting#Colors
enum class LegacyTextColor(val hex: String) {
    BLACK("#000000"),
    DARK_BLUE("#0000AA"),
    GREEN("#00AA00"),
    CYAN("#00AAAA"),
    DARK_RED("#AA0000"),
    PURPLE("#AA00AA"),
    ORANGE("#FFAA00"),
    GRAY("#AAAAAA"),
    DARK_GRAY("#555555"),
    BLUE("#5555FF"),
    LIME("#55FF55"),
    AQUA("#55FFFF"),
    RED("#FF5555"),
    PINK("#FF55FF"),
    YELLOW("#FFFF55"),
    WHITE("#FFFFFF"),
}
