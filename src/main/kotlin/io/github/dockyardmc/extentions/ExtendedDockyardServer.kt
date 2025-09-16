package io.github.dockyardmc.extentions

import io.github.dockyardmc.DockyardServer

fun broadcastMessage(message: String, isSystem: Boolean = false) {
    DockyardServer.sendMessage(message, isSystem)
}
