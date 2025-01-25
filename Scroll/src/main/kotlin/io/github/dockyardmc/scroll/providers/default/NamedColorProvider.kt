package io.github.dockyardmc.scroll.providers.default

import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.ScrollUtil
import io.github.dockyardmc.scroll.extensions.replaceMultiple
import io.github.dockyardmc.scroll.providers.ClosingNamedFormatProvider
import io.github.dockyardmc.scroll.providers.FormatProviderContext

class NamedColorProvider :
    ClosingNamedFormatProvider("red", ScrollUtil.colorTags.keys.toList().map { it.replaceMultiple(listOf("<", ">"), "") }) {

    override fun formatNormal(context: FormatProviderContext, component: Component) {
        component.resetFormatting(false)
        val color = ScrollUtil.colorTags[context.token] ?: throw IllegalStateException("Invalid legacy text color: ${context.token}")
        component.color = color
    }

    override fun formatClosing(context: FormatProviderContext, component: Component) {
        component.resetFormatting(false)
    }

}