package io.github.dockyardmc.protocol

import io.github.dockyardmc.data.HashHolder
import kotlin.reflect.KClass

interface DataComponentHashable {
    fun hashStruct(): HashHolder {
        return unsupported(this)
    }

    fun unsupported(kclass: KClass<*>): HashHolder {
        throw UnsupportedOperationException("${kclass.simpleName} is not supported yet! If you require use of this component please open an issue")
    }

    fun unsupported(any: Any): HashHolder {
        return unsupported(any::class)
    }
}