package io.github.dockyardmc.scroll.providers.default

import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.providers.ClosingNamedFormatProvider
import io.github.dockyardmc.scroll.providers.FormatProviderContext

class BoldProvider: ClosingNamedFormatProvider("bold", listOf("b")) {

    override fun formatNormal(context: FormatProviderContext, component: Component) {
        component.bold = true
    }

    override fun formatClosing(context: FormatProviderContext, component: Component) {
        component.bold = null
    }

}