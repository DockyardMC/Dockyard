package io.github.dockyardmc.utils

import CustomLogType
import io.github.dockyardmc.scroll.extensions.stripComponentTags
import log

object Console {
    private val chatLog = CustomLogType("\uD83D\uDCAC Chat", AnsiPair.WHITE)

    //TODO: Color the console output by the nearest ANSI color to the chat color
    fun sendMessage(message: String) {
        log(message.stripComponentTags(), chatLog)
    }
}