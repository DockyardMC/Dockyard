package io.github.dockyardmc.utils

import cz.lukynka.prettylog.AnsiPair
import cz.lukynka.prettylog.CustomLogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.scroll.extensions.stripComponentTags

object Console {
    private val chatLog = CustomLogType("\uD83D\uDCAC Chat", AnsiPair.WHITE)

    //TODO: Color the console output by the nearest ANSI color to the chat color
    fun sendMessage(message: String) {
        log(message.stripComponentTags(), chatLog)
    }
}