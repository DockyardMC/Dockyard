package io.github.dockyardmc.utils

import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.entities.EntityMetaIndex
import io.github.dockyardmc.entities.EntityMetadata

fun mergeEntityMetadata(base: Entity, layer: List<EntityMetadata>?): List<EntityMetadata> {
    if(layer == null) return base.metadata.values.toList()
    val metadata = base.metadata.values
    val final = mutableMapOf<EntityMetaIndex, EntityMetadata>()
    metadata.forEach {
        val index = it.index
        final[index] = it
    }
    layer.forEach {
        val index = it.index
        final[index] = it
    }
    return final.values.toList()
}