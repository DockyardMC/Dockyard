package io.github.dockyardmc.utils

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.metadata.Metadata

fun mergeEntityMetadata(base: Entity, layer: Map<Metadata.MetadataDefinitionEntry<*>, Metadata.MetadataDefinition.Value<*>>?): List<Metadata.MetadataDefinition.Value<*>> {
    if (layer == null) return base.metadata.getValues().values.toList()

    val metadata = base.metadata.getValues()
    val final = mutableMapOf<Metadata.MetadataDefinitionEntry<*>, Metadata.MetadataDefinition.Value<*>>()
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