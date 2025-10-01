package io.github.dockyardmc.entity.metadata

import io.github.dockyardmc.codec.ComponentCodecs
import io.github.dockyardmc.tide.stream.StreamCodec
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import java.util.concurrent.atomic.AtomicInteger

object MetadataType {
    val MAX_INDEX: Object2IntOpenHashMap<String> = Object2IntOpenHashMap()
    private val serializers = mutableListOf<MetadataSerializer<*>>()
    private val metadataType = AtomicInteger()

    val BYTE = next(StreamCodec.BYTE)
    val VAR_INT = next(StreamCodec.VAR_INT)

    val OPTIONAL_COMPONENT = next(ComponentCodecs.STREAM.optional())
    val FLOAT = next(StreamCodec.FLOAT)

    data class MetadataSerializer<T>(val type: Int, val streamCodec: StreamCodec<T>)

    fun <T> next(streamCodec: StreamCodec<T>): MetadataSerializer<T> {
        return MetadataSerializer<T>(metadataType.getAndIncrement(), streamCodec)
    }
}