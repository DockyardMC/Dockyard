package io.github.dockyardmc.protocol

import io.github.dockyardmc.data.HashHolder

interface DataComponentHashable {
    fun hashStruct(): HashHolder
}