package io.github.dockyardmc.utils

import cz.lukynka.prettylog.CustomLogType
import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.extentions.broadcastMessage

fun debug(text: String, toChat: Boolean = false, logType: CustomLogType = LogType.DEBUG) {
    if(InstrumentationUtils.isDebuggerAttached() && toChat) {
        DockyardServer.broadcastMessage("<#5b6070>(⚑) <#9097ad>$text")
    }
    if(DockyardServer.debug) log(text, logType)
}