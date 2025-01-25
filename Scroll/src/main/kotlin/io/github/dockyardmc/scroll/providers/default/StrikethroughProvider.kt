package io.github.dockyardmc.scroll.providers.default

import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.providers.ClosingNamedFormatProvider
import io.github.dockyardmc.scroll.providers.FormatProviderContext

class StrikethroughProvider: ClosingNamedFormatProvider("strikethrough", listOf("s", "strike")) {

    override fun formatNormal(context: FormatProviderContext, component: Component) {
        component.strikethrough = true
    }

    override fun formatClosing(context: FormatProviderContext, component: Component) {
        component.strikethrough = null
    }

}