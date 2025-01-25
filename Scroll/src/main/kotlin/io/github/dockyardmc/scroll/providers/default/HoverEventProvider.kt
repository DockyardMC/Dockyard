package io.github.dockyardmc.scroll.providers.default

import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.HoverAction
import io.github.dockyardmc.scroll.HoverEvent
import io.github.dockyardmc.scroll.Scroll
import io.github.dockyardmc.scroll.providers.FormatProviderContext
import io.github.dockyardmc.scroll.providers.NamedFormatProvider

class HoverEventProvider: NamedFormatProvider("hover") {

    override fun format(context: FormatProviderContext, component: Component) {
        val action = HoverAction.valueOf(context.getArgument(0).uppercase())
        val value = Scroll.parse(context.getArgument(1))
        val hoverEvent = HoverEvent(action, value)

        component.hoverEvent = hoverEvent
    }
}