package io.github.dockyardmc.scroll.providers

import io.github.dockyardmc.scroll.Component

abstract class FormatProvider() {
    abstract fun matches(token: String): Boolean
    abstract fun format(context: FormatProviderContext, component: Component)
}