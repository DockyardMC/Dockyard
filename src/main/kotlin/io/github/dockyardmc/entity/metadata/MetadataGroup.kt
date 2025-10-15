package io.github.dockyardmc.entity.metadata

import io.github.dockyardmc.entity.metadata.Metadata.MetadataDefinition
import io.github.dockyardmc.entity.metadata.MetadataType.MetadataSerializer
import java.util.concurrent.atomic.AtomicInteger

abstract class MetadataGroup(initialValue: Int = 0) {
    constructor(parent: MetadataGroup) : this(parent.counter.get())

    protected val counter = AtomicInteger(initialValue)

    protected fun <T> define(type: MetadataSerializer<T>, default: T): MetadataDefinition<T> {
        return MetadataDefinition(counter.getAndIncrement(), type, default)
    }

    protected fun <T> bitmask(parent: MetadataDefinition<Byte>, bitMask: Byte, defaultValue: T): Metadata.BitmaskFlagDefinition<T> {
        return Metadata.BitmaskFlagDefinition<T>(parent, bitMask, defaultValue)
    }
}