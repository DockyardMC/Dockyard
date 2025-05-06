package io.github.dockyardmc.data

import io.github.dockyardmc.protocol.DataComponentHashable
import io.github.dockyardmc.protocol.NetworkWritable

abstract class DataComponent(val isSingleField: Boolean = false) : NetworkWritable, DataComponentHashable {

    fun getId(): Int {
        return getIdOrNull() ?: throw NoSuchElementException("Data Component Registry does not have this component")
    }

    fun getIdOrNull(): Int? {
        return DataComponentRegistry.dataComponentsByIdReversed.getOrDefault(this::class, null)
    }
}