package io.github.dockyardmc.plugins

import io.github.dockyardmc.DockyardServer

interface DockyardPlugin {

    var name: String
    var author: String
    var version: String

    fun load(server: DockyardServer)

    fun unload(server: DockyardServer)

}