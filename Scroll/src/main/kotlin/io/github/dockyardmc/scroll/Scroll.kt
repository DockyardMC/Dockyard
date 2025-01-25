package io.github.dockyardmc.scroll

import io.github.dockyardmc.scroll.providers.FormatProvider
import io.github.dockyardmc.scroll.providers.default.BoldProvider
import io.github.dockyardmc.scroll.providers.default.ClickEventProvider
import io.github.dockyardmc.scroll.providers.default.FontProvider
import io.github.dockyardmc.scroll.providers.default.HexColorProvider
import io.github.dockyardmc.scroll.providers.default.HoverEventProvider
import io.github.dockyardmc.scroll.providers.default.ItalicsProvider
import io.github.dockyardmc.scroll.providers.default.KeybindProvider
import io.github.dockyardmc.scroll.providers.default.NamedColorProvider
import io.github.dockyardmc.scroll.providers.default.ObfuscatedProvider
import io.github.dockyardmc.scroll.providers.default.ResetProvider
import io.github.dockyardmc.scroll.providers.default.StrikethroughProvider
import io.github.dockyardmc.scroll.providers.default.TranslateProvider
import io.github.dockyardmc.scroll.providers.default.UnderlinedProvider

object Scroll {

    val defaultFormatProviders = listOf<FormatProvider>(
        HexColorProvider(),
        BoldProvider(),
        ItalicsProvider(),
        UnderlinedProvider(),
        StrikethroughProvider(),
        ObfuscatedProvider(),
        NamedColorProvider(),
        FontProvider(),
        ResetProvider(),
        TranslateProvider(),
        KeybindProvider(),
        ClickEventProvider(),
        HoverEventProvider()
    )

    private val defaultParser = ScrollParser(defaultFormatProviders)

    fun parse(input: String): Component {
        return defaultParser.parse(input)
    }
}
