package io.github.dockyardmc.entity.metadata

import io.github.dockyardmc.codec.ComponentCodecs
import io.github.dockyardmc.codec.LocationCodecs
import io.github.dockyardmc.codec.RegistryCodec
import io.github.dockyardmc.entity.entities.Armadillo
import io.github.dockyardmc.entity.entities.CopperGolem
import io.github.dockyardmc.entity.entities.Sniffer
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.maths.Quaternion
import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.EntityPose
import io.github.dockyardmc.protocol.types.ResolvableProfile
import io.github.dockyardmc.registry.registries.*
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
    val LONG = next(StreamCodec.LONG)
    val FLOAT = next(StreamCodec.FLOAT)
    val STRING = next(StreamCodec.STRING)
    val COMPONENT = next(ComponentCodecs.STREAM)
    val OPTIONAL_COMPONENT = next(ComponentCodecs.STREAM.optional())
    val ITEM_STACK = next(ItemStack.STREAM_CODEC)
    val BOOLEAN = next(StreamCodec.BOOLEAN)
    val ROTATION = next(Vector3f.STREAM_CODEC)
    val BLOCK_POSITION = next(LocationCodecs.BLOCK_POSITION)
    val OPTIONAL_BLOCK_POSITION = next(LocationCodecs.BLOCK_POSITION.optional())
    val DIRECTION = next(StreamCodec.enum<Direction>())
    val OPTIONAL_UUID = next(StreamCodec.UUID.optional())
    val BLOCK_STATE = next(Block.STREAM_CODEC)
    val OPTIONAL_BLOCK_STATE = next(Block.STREAM_CODEC.optional())
    val PARTICLE = next(RegistryCodec.stream(ParticleRegistry))
    val PARTICLE_LIST = next(RegistryCodec.stream(ParticleRegistry).list())
    val VILLAGER_DATA = skip()
    val OPTIONAL_VAR_INT = next(StreamCodec.VAR_INT.optional())
    val POSE = next(StreamCodec.enum<EntityPose>())
    val CAT_VARIANT = next(RegistryCodec.stream(CatVariantRegistry))
    val COW_VARIANT = next(RegistryCodec.stream(CowVariantRegistry))
    val WOLF_VARIANT = next(RegistryCodec.stream(WolfVariantRegistry))
    val WOLF_SOUND_VARIANT = next(RegistryCodec.stream(WolfSoundVariantRegistry))
    val FROG_VARIANT = next(RegistryCodec.stream(FrogVariantRegistry))
    val PIG_VARIANT = next(RegistryCodec.stream(PigVariantRegistry))
    val CHICKEN_VARIANT = next(RegistryCodec.stream(ChickenVariantRegistry))
    val OPTIONAL_GLOBAL_POSITION = skip() // unused in vanilla?
    val PAINTING_VARIANT = next(RegistryCodec.stream(PaintingVariantRegistry))
    val SNIFFER_STATE = next(StreamCodec.enum<Sniffer.State>())
    val ARMADILLO_STATE = next(StreamCodec.enum<Armadillo.State>())
    val COPPER_GOLEM_STATE = next(StreamCodec.enum<CopperGolem.State>())
    val COPPER_GOLEM_WEATHER_STATE = next(StreamCodec.enum<CopperGolem.WeatherState>())
    val VECTOR_3F = next(Vector3f.STREAM_CODEC)
    val QUATERNION = next(Quaternion.STREAM_CODEC)
    val RESOLVABLE_PROFILE = next(ResolvableProfile.STREAM_CODEC)

    data class MetadataSerializer<T>(val type: Int, val streamCodec: StreamCodec<T>)

    fun <T> next(streamCodec: StreamCodec<T>): MetadataSerializer<T> {
        return MetadataSerializer<T>(metadataType.getAndIncrement(), streamCodec)
    }

    fun skip() {
        metadataType.getAndIncrement()
    }
}