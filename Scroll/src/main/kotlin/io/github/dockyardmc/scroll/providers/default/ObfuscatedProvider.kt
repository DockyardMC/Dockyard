package io.github.dockyardmc.scroll.providers.default

import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.providers.ClosingNamedFormatProvider
import io.github.dockyardmc.scroll.providers.FormatProviderContext

class ObfuscatedProvider: ClosingNamedFormatProvider("obfuscated", listOf("obf", "o")) {

    override fun formatNormal(context: FormatProviderContext, component: Component) {
        component.obfuscated = true
    }

    override fun formatClosing(context: FormatProviderContext, component: Component) {
        component.obfuscated = null
    }

}