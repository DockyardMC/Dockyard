package io.github.dockyardmc.utils

import cz.lukynka.prettylog.CustomLogType
import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.extentions.broadcastMessage

fun debug(text: String, toChat: Boolean = false, logType: CustomLogType = LogType.DEBUG) {
    log(text, logType)
    if(toChat) {
        DockyardServer.broadcastMessage("<#fc6203>[DEBUG] $text")
    }
//    if(DockyardServer.debug) log(text, logType)
}