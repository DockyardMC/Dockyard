package io.github.dockyardmc.data

import io.github.dockyardmc.protocol.NetworkWritable

abstract class DataComponent : NetworkWritable {

    fun getId(): Int {
        return getIdOrNull() ?: throw NoSuchElementException("Data Component Registry does not have this component")
    }

    fun getIdOrNull(): Int? {
        return DataComponentRegistry.dataComponentsByIdReversed.getOrDefault(this::class, null)
    }

//    abstract fun hash(hasher: Hasher): Int
}