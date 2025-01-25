package io.github.dockyardmc.scroll.providers.default

import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.providers.FormatProviderContext
import io.github.dockyardmc.scroll.providers.NamedFormatProvider

class ResetProvider: NamedFormatProvider("reset", listOf("r")) {

    override fun format(context: FormatProviderContext, component: Component) {
        component.resetFormatting(true)
    }
}