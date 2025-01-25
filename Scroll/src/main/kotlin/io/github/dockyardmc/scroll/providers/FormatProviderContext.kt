package io.github.dockyardmc.scroll.providers

import io.github.dockyardmc.scroll.ScrollUtil


data class FormatProviderContext(val token: String, val textUntilNextTag: String, val fullMessage: String) {

    fun getArgument(index: Int): String {
        val arguments = ScrollUtil.getArguments(token)
        return arguments.getOrNull(index) ?: throw IndexOutOfBoundsException("Index $index is out of bounds for ${arguments.size} arguments")
    }
}