package io.github.dockyardmc.scroll.providers

import io.github.dockyardmc.scroll.Component

abstract class ClosingNamedFormatProvider(name: String, aliases: List<String>): NamedFormatProvider(name, aliases) {

    override fun matches(token: String): Boolean {
        return aliases.contains(token.replace("<", "").replace(">", "").split(":")[0])
                || aliases.contains(token.replace("</", "").replace(">", "").split(":")[0])
                || token.replace("<", "").replace(">", "").split(":")[0] == name
                || token.replace("</", "").replace(">", "").split(":")[0] == name
    }

    override fun format(context: FormatProviderContext, component: Component) {
        if(context.token.startsWith("</")) formatClosing(context, component) else formatNormal(context, component)
    }

    abstract fun formatNormal(context: FormatProviderContext, component: Component)
    abstract fun formatClosing(context: FormatProviderContext, component: Component)
}