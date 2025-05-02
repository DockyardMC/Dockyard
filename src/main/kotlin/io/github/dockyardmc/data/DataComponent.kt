package io.github.dockyardmc.data

import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.tide.Codec

abstract class DataComponent(val isSingleField: Boolean = false) : NetworkWritable {

    fun getId(): Int {
        return getIdOrNull() ?: throw NoSuchElementException("Data Component Registry does not have this component")
    }

    fun getIdOrNull(): Int? {
        return DataComponentRegistry.dataComponentsByIdReversed.getOrDefault(this::class, null)
    }

    abstract fun getHashCodec(): Codec<out DataComponent>

//    abstract fun hash(hasher: Hasher): Int
}