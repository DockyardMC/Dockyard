package io.github.dockyardmc.plugins

import io.github.dockyardmc.DockyardServer

interface DockyardPlugin {
    val name: String
    val author: String
    val version: String

    fun load(server: DockyardServer)

    fun unload(server: DockyardServer)
}