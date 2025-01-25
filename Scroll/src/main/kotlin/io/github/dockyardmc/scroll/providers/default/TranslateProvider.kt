package io.github.dockyardmc.scroll.providers.default

import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.providers.FormatProviderContext
import io.github.dockyardmc.scroll.providers.NamedFormatProvider

class TranslateProvider: NamedFormatProvider("translate") {

    override fun format(context: FormatProviderContext, component: Component) {
        val key = context.getArgument(0)
        component.translate = key
    }
}