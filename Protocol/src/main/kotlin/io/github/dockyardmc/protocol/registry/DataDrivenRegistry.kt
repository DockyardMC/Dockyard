package io.github.dockyardmc.protocol.registry

import java.io.InputStream

interface DataDrivenRegistry: Registry {
    fun initialize(inputStream: InputStream)
}