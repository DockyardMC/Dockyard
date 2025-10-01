package io.github.dockyardmc.entity.metadata

import io.github.dockyardmc.entity.metadata.Metadata.MetadataDefinition
import io.github.dockyardmc.entity.metadata.MetadataType.MetadataSerializer
import java.util.concurrent.atomic.AtomicInteger

object Metadata : MetadataGroup() {

    val ENTITY_FLAGS = define(MetadataType.VAR_INT, 0)
    val AIR_TICKS = define(MetadataType.VAR_INT, 300)
    val CUSTOM_NAME = define(MetadataType.OPTIONAL_COMPONENT, null)

    object Interaction : MetadataGroup(Metadata) {
        val WIDTH = define(MetadataType.FLOAT, 1f)
        val HEIGHT = define(MetadataType.FLOAT, 1f)
    }

    data class MetadataDefinition<T>(val index: Int, val type: MetadataSerializer<T>, val default: T)
}

abstract class MetadataGroup(initialValue: Int = 0) {
    constructor(parent: MetadataGroup) : this(parent.counter.get())

    protected val counter = AtomicInteger(initialValue)

    protected fun <T> define(type: MetadataSerializer<T>, default: T): MetadataDefinition<T> {
        return MetadataDefinition(counter.getAndIncrement(), type, default)
    }
}