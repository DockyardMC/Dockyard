package io.github.dockyardmc.utils

import cz.lukynka.prettylog.AnsiPair
import cz.lukynka.prettylog.CustomLogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.scroll.extensions.stripComponentTags
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object Console {
    private val chatLog = CustomLogType("\uD83D\uDCAC Chat", AnsiPair.WHITE)

    //TODO: Color the console output by the nearest ANSI color to the chat color
    @OptIn(DelicateCoroutinesApi::class)
    fun sendMessage(message: String) {
        GlobalScope.launch {
            log(message.stripComponentTags(), chatLog)
        }
    }
}