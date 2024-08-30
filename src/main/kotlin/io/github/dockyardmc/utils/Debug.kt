package io.github.dockyardmc.utils

import cz.lukynka.prettylog.CustomLogType
import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.DockyardServer

fun debug(text: String, logType: CustomLogType = LogType.DEBUG) {
    log(text, logType)
//    if(DockyardServer.debug) log(text, logType)
}