package io.github.dockyardmc.scroll.providers.default

import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.providers.ClosingNamedFormatProvider
import io.github.dockyardmc.scroll.providers.FormatProviderContext

class FontProvider: ClosingNamedFormatProvider("font", listOf()) {

    override fun formatNormal(context: FormatProviderContext, component: Component) {
        val font = context.getArgument(0)
        component.font = font

    }

    override fun formatClosing(context: FormatProviderContext, component: Component) {
        component.font = null
    }
}

