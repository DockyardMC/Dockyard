package io.github.dockyardmc.protocol

import io.github.dockyardmc.data.HashHolder
import kotlin.reflect.KClass

interface DataComponentHashable {
    fun hashStruct(): HashHolder

    fun unsupported(kclass: KClass<*>): HashHolder {
        throw UnsupportedOperationException("${kclass.simpleName} is not supported yet!")
    }
}