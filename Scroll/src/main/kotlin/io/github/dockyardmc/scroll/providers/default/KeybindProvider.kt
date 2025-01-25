package io.github.dockyardmc.scroll.providers.default

import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.providers.FormatProviderContext
import io.github.dockyardmc.scroll.providers.NamedFormatProvider

class KeybindProvider: NamedFormatProvider("keybind") {

    override fun format(context: FormatProviderContext, component: Component) {
        val keybind = context.getArgument(0)
        component.keybind = keybind
    }
}