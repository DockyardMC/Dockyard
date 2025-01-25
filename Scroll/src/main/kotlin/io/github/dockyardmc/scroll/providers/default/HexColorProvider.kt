package io.github.dockyardmc.scroll.providers.default

import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.replaceMultiple
import io.github.dockyardmc.scroll.providers.FormatProvider
import io.github.dockyardmc.scroll.providers.FormatProviderContext

class HexColorProvider: FormatProvider() {

    override fun matches(token: String): Boolean {
        return token.replace("<", "").replace(">", "").split(":")[0].startsWith("#")
    }

    override fun format(context: FormatProviderContext, component: Component) {
        component.resetFormatting(false)
        component.color = context.token.replaceMultiple(listOf("<", ">"), "")
    }
}