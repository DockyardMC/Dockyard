package io.github.dockyardmc.scroll.providers.default

import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.providers.ClosingNamedFormatProvider
import io.github.dockyardmc.scroll.providers.FormatProviderContext

class UnderlinedProvider: ClosingNamedFormatProvider("underlined", listOf("underline", "u")) {

    override fun formatNormal(context: FormatProviderContext, component: Component) {
        component.underlined = true
    }

    override fun formatClosing(context: FormatProviderContext, component: Component) {
        component.underlined = null
    }

}