package io.github.dockyardmc.entity.metadata

import io.github.dockyardmc.codec.ComponentCodecs
import io.github.dockyardmc.codec.LocationCodecs
import io.github.dockyardmc.codec.RegistryCodec
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.maths.Quaternion
import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.EntityPose
import io.github.dockyardmc.registry.registries.PaintingVariantRegistry
import io.github.dockyardmc.registry.registries.ParticleRegistry
import io.github.dockyardmc.tide.stream.StreamCodec
import io.github.dockyardmc.world.block.Block
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import java.util.concurrent.atomic.AtomicInteger

object MetadataType {
    val MAX_INDEX: Object2IntOpenHashMap<String> = Object2IntOpenHashMap()
    private val serializers = mutableListOf<MetadataSerializer<*>>()
    private val metadataType = AtomicInteger()

    val BYTE = next(StreamCodec.BYTE)
    val VAR_INT = next(StreamCodec.VAR_INT)
    val OPTIONAL_VAR_INT = next(StreamCodec.VAR_INT.optional())
    val BOOLEAN = next(StreamCodec.BOOLEAN)
    val POSE = next(StreamCodec.enum<EntityPose>())
    val VECTOR_3F = next(Vector3f.STREAM_CODEC)
    val QUATERNION = next(Quaternion.STREAM_CODEC)
    val BLOCK_STATE = next(Block.STREAM_CODEC)
    val OPTIONAL_BLOCK_STATE = next(Block.STREAM_CODEC.optional())
    val ITEM_STACK = next(ItemStack.STREAM_CODEC)
    val BLOCK_POSITION = next(LocationCodecs.BLOCK_POSITION)
    val OPTIONAL_BLOCK_POSITION = next(LocationCodecs.BLOCK_POSITION.optional())
    val PARTICLE = next(RegistryCodec.stream(ParticleRegistry))
    val DIRECTION = next(StreamCodec.enum<Direction>())
    val PAINTING_VARIANT = next(RegistryCodec.stream(PaintingVariantRegistry))

    val OPTIONAL_COMPONENT = next(ComponentCodecs.STREAM.optional())
    val COMPONENT = next(ComponentCodecs.STREAM)
    val FLOAT = next(StreamCodec.FLOAT)

    data class MetadataSerializer<T>(val type: Int, val streamCodec: StreamCodec<T>)

    fun <T> next(streamCodec: StreamCodec<T>): MetadataSerializer<T> {
        return MetadataSerializer<T>(metadataType.getAndIncrement(), streamCodec)
    }
}