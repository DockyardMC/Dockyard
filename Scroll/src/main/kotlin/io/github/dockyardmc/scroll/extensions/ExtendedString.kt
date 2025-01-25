package io.github.dockyardmc.scroll.extensions

import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.Scroll

fun String.toComponent(): Component {
    return Scroll.parse(this)
}

fun String.scrollSanitized(): String {
    var out = ""
    this.forEachIndexed { index, char ->
        out += (if (char.toString() == "<") "\\<" else char.toString())
    }
    return out
}

fun String.stripComponentTags(): String {
    return this.toComponent().stripStyling()
}

fun String.replaceMultiple(delimiters: Collection<String>, replacement: String): String {
    var result = this
    delimiters.forEach { delimiter ->
        result = result.replace(delimiter, replacement)
    }
    return result
}

fun String.replaceMultiple(replacement: String, vararg delimiter: String): String {
    return this.replaceMultiple(delimiter.toList(), replacement)
}