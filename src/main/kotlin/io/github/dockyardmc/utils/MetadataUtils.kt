package io.github.dockyardmc.utils

import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.entities.EntityMetadataType
import io.github.dockyardmc.entities.EntityMetadata

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