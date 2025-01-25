package io.github.dockyardmc.scroll.providers

abstract class NamedFormatProvider(val name: String, val aliases: List<String>): FormatProvider() {

    override fun matches(token: String): Boolean {
        return aliases.contains(token.replace("<", "").replace(">", "").split(":")[0])
                || token.replace("<", "").replace(">", "").split(":")[0] == name
    }
    constructor(name: String): this(name, listOf())
}