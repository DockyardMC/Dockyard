package io.github.dockyardmc.scroll

import io.github.dockyardmc.scroll.providers.FormatProvider
import io.github.dockyardmc.scroll.providers.FormatProviderContext

data class ScrollParser(val formatProviders: Collection<FormatProvider>) {

    fun parse(input: String): Component {
        val components = mutableListOf<Component>()
        val tokens = ScrollUtil.splitDelimiter(input, "<", ">")

        val styleComponent = Component()

        tokens.forEach { token ->
            var currentComponent = Component()
            var foundProvider = false
            var isInteractiveProvider = false
            var isPrefilledTextProvider = false

            formatProviders.forEach { provider ->
                if (token.startsWith("<") && token.endsWith(">")) {
                    if (provider.matches(token)) {
                        provider.format(FormatProviderContext(token, token, input), styleComponent)
                        foundProvider = true
                        if(styleComponent.translate != null || styleComponent.keybind != null || styleComponent.insertion != null) {
                            foundProvider = false
                            isInteractiveProvider = true
                        }
                        if(styleComponent.text != null) {
                            foundProvider = false
                            isPrefilledTextProvider = true
                        }
                    }
                }
            }

            if (foundProvider) return@forEach

            if (token.isEmpty()) return@forEach
            currentComponent.text = token

            if(isInteractiveProvider) {
                currentComponent.text = null
                currentComponent.keybind = styleComponent.keybind
                currentComponent.translate = styleComponent.translate
                currentComponent.insertion = styleComponent.insertion

                styleComponent.keybind = null
                styleComponent.translate = null
                styleComponent.insertion = null
            }

            if(isPrefilledTextProvider) {
                currentComponent.text = styleComponent.text
                styleComponent.text = null
            }

            currentComponent.color = styleComponent.color
            currentComponent.bold = styleComponent.bold
            currentComponent.italic = styleComponent.italic
            currentComponent.underlined = styleComponent.underlined
            currentComponent.font = styleComponent.font
            currentComponent.obfuscated = styleComponent.obfuscated
            currentComponent.strikethrough = styleComponent.strikethrough
            currentComponent.clickEvent = styleComponent.clickEvent
            currentComponent.hoverEvent = styleComponent.hoverEvent


            components.add(currentComponent)
        }
        val final = Component.compound(components)
        return final
    }
}