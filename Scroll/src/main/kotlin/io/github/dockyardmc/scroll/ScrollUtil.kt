package io.github.dockyardmc.scroll

import java.util.regex.Pattern

object ScrollUtil {

    val colorTags = mutableMapOf<String, String>(
        "<red>" to LegacyTextColor.RED.hex,
        "<yellow>" to LegacyTextColor.YELLOW.hex,
        "<lime>" to LegacyTextColor.LIME.hex,
        "<aqua>" to LegacyTextColor.AQUA.hex,
        "<blue>" to LegacyTextColor.BLUE.hex,
        "<pink>" to LegacyTextColor.PINK.hex,

        "<dark red>" to LegacyTextColor.DARK_RED.hex,
        "<dark_red>" to LegacyTextColor.DARK_RED.hex,
        "<orange>" to LegacyTextColor.ORANGE.hex,
        "<gold>" to LegacyTextColor.ORANGE.hex,
        "<green>" to LegacyTextColor.GREEN.hex,
        "<cyan>" to LegacyTextColor.CYAN.hex,
        "<dark blue>" to LegacyTextColor.DARK_BLUE.hex,
        "<dark_blue>" to LegacyTextColor.DARK_BLUE.hex,
        "<purple>" to LegacyTextColor.DARK_BLUE.hex,

        "<white>" to LegacyTextColor.WHITE.hex,
        "<gray>" to LegacyTextColor.GRAY.hex,
        "<dark gray>" to LegacyTextColor.DARK_GRAY.hex,
        "<dark_gray>" to LegacyTextColor.DARK_GRAY.hex,
        "<black>" to LegacyTextColor.BLACK.hex,
    )

    fun splitDelimiter(string: String, start: String, end: String): MutableList<String> {
        val result = mutableListOf<String>()

        var out = ""
        var open = false
        var insideQuotes = false
        string.forEachIndexed { index, it ->
            if (insideQuotes && it != '\'') {
                out = "$out${it}"; return@forEachIndexed
            }
            if (out.startsWith("<")) {
                if (it == '\'') {
                    if (insideQuotes) {
                        val nextChar = getCharacterAfter(string, index)
                            ?: throw IllegalStateException("end of quoted string unexpectedly")
                        insideQuotes = !(nextChar == '>' || nextChar == ':')
                    } else {
                        insideQuotes = true
                    }
                }
            }

            if (it == start[0] && getCharacterBefore(string, index).toString() != "\\") {
                open = true
                if (out.isNotEmpty()) {
                    result.add(out)
                }
                out = "$it"
                return@forEachIndexed
            }
            if (it.toString() == "\\" && getCharacterAfter(string, index) == start[0]) return@forEachIndexed
            out = "$out${it}"

            if (it == end[0] && open) {
                open = false
                if (out.isNotEmpty()) {
                    result.add(out)
                }
                out = ""
            }
            if (index == string.length - 1) {
                result.add(out)
            }
        }
        return result
    }

    fun getCharacterBefore(string: String, currentIndex: Int): Char? {
        val index = currentIndex - 1
        val value = if (index < 0) null else string[index]
        return value
    }

    fun getCharacterAfter(string: String, currentIndex: Int): Char? {
        val index = currentIndex + 1
        val value = if (index > string.toMutableList().size) null else string[index]
        return value
    }

    fun getCharacterAfterNew(string: String, currentIndex: Int): Char? {
        val index = currentIndex + 1
        val value = if (index >= string.toMutableList().size) null else string[index]
        return value
    }

    fun getArguments(input: String): List<String> {
        val split = mutableListOf<String>()

        var current = ""
        var isInsideQuotes = false

        val validQuotesEndChars = listOf<Char>('>', ':')

        input.forEachIndexed { index, char ->
            val nextChar = getCharacterAfterNew(input, index)

            if(char == '\'') {
                if(!isInsideQuotes) {
                    isInsideQuotes = true
                }
                else if(validQuotesEndChars.contains(nextChar)) {
                    isInsideQuotes = false
                } else {
                    current += char
                }
            } else if(char == ':') {
                if(!isInsideQuotes) {
                    split.add(current)
                    current = ""
                } else {
                    current += char
                }
            } else {
                current += char
            }
        }

        if(current.endsWith(">")) {
            split.add((if(!isInsideQuotes) current else current.removePrefix("<")).removeSuffix(">"))
            current = ""
        }

        return split.subList(1, split.size)
    }
}