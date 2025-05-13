package io.github.dockyardmc.utils

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.metadata.EntityMetadata
import io.github.dockyardmc.entity.metadata.EntityMetadataType

fun mergeEntityMetadata(base: Entity, layer: Map<EntityMetadataType, EntityMetadata>?): List<EntityMetadata> {
    if(layer == null) return base.metadata.values.values.toList()
    val metadata = base.metadata.values
    val final = mutableMapOf<EntityMetadataType, EntityMetadata>()
    metadata.forEach {
        val index = it.key
        final[index] = it.value
    }
    layer.forEach {
        val index = it.key
        final[index] = it.value
    }
    return final.values.toList()
}