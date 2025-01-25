package io.github.dockyardmc.scroll.providers.default

import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.providers.ClosingNamedFormatProvider
import io.github.dockyardmc.scroll.providers.FormatProviderContext

class ItalicsProvider: ClosingNamedFormatProvider("italic", listOf("italic", "italics", "i")) {

    override fun formatNormal(context: FormatProviderContext, component: Component) {
        component.italic = true
    }

    override fun formatClosing(context: FormatProviderContext, component: Component) {
        component.italic = null
    }

}