package io.github.dockyardmc.protocol

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import kotlin.reflect.KClass

interface DataComponentHashable {
    fun hashStruct(): HashHolder {
        return StaticHash(CRC32CHasher.EMPTY)
    }

    fun unsupported(kclass: KClass<*>): HashHolder {
        throw UnsupportedOperationException("${kclass.simpleName} is not supported yet! If you require use of this component please open an issue")
    }
}