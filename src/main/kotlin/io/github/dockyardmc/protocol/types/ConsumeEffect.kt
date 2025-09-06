package io.github.dockyardmc.protocol.types

import io.github.dockyardmc.codec.RegistryCodec
import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.effects.AppliedPotionEffect
import io.github.dockyardmc.extentions.read
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.protocol.DataComponentHashable
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.registry.registries.PotionEffect
import io.github.dockyardmc.registry.registries.PotionEffectRegistry
import io.github.dockyardmc.sounds.CustomSoundEvent
import io.github.dockyardmc.sounds.SoundEvent
import io.github.dockyardmc.tide.codec.Codec
import io.github.dockyardmc.tide.codec.StructCodec
import io.github.dockyardmc.tide.stream.StreamCodec
import io.github.dockyardmc.utils.MutableBiMap
import io.netty.buffer.ByteBuf
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

interface ConsumeEffect : DataComponentHashable {

    fun getCodec(): Codec<out ConsumeEffect>
    fun getStreamCodec(): StreamCodec<out ConsumeEffect>

    companion object : NetworkReadable<ConsumeEffect> {
        val effects = MutableBiMap<Int, KClass<out ConsumeEffect>>()
        private val protocolIdCounter = AtomicInteger()

        init {
            register(ApplyEffects::class)
            register(RemoveEffects::class)
            register(ClearAllEffects::class)
            register(TeleportRandomly::class)
            register(PlaySound::class)
        }

        override fun read(buffer: ByteBuf): ConsumeEffect {
            val id = buffer.readVarInt()
            val kclass = effects.getByKey(id)
            return kclass.read(buffer)
        }

        private fun register(kclass: KClass<out ConsumeEffect>) {
            effects.put(protocolIdCounter.getAndIncrement(), kclass)
        }
    }

    data class ApplyEffects(val effects: List<AppliedPotionEffect>, val probability: Float) : ConsumeEffect {

        init {
            require(probability in 0f..1f) { "Probability must be between 0f and 1f" }
        }

        override fun getCodec(): Codec<out ConsumeEffect> {
            return CODEC
        }

        override fun getStreamCodec(): StreamCodec<out ConsumeEffect> {
            return STREAM_CODEC
        }

        companion object {
            const val ID = 0
            const val DEFAULT_PROBABILITY = 1f

            val STREAM_CODEC = StreamCodec.of(
                AppliedPotionEffect.STREAM_CODEC.list(), ApplyEffects::effects,
                StreamCodec.FLOAT, ApplyEffects::probability,
                ::ApplyEffects
            )

            val CODEC = StructCodec.of(
                "effects", AppliedPotionEffect.CODEC.list(), ApplyEffects::effects,
                "probability", Codec.FLOAT.default(DEFAULT_PROBABILITY), ApplyEffects::probability,
                ::ApplyEffects
            )
        }

        override fun hashStruct(): HashHolder {
            return CRC32CHasher.of {
                structList("effects", effects, AppliedPotionEffect::hashStruct)
                default("probability", 1f, probability, CRC32CHasher::ofFloat)
            }
        }

    }

    data class RemoveEffects(val effects: List<PotionEffect>) : ConsumeEffect {

        override fun getCodec(): Codec<out ConsumeEffect> {
            return CODEC
        }

        override fun getStreamCodec(): StreamCodec<out ConsumeEffect> {
            return STREAM_CODEC
        }

        companion object {
            const val ID = 1

            val STREAM_CODEC = StreamCodec.of(
                RegistryCodec.stream(PotionEffectRegistry).list(), RemoveEffects::effects,
                ::RemoveEffects
            )

            val CODEC = StructCodec.of(
                "effects", RegistryCodec.codec(PotionEffectRegistry).list(), RemoveEffects::effects,
                ::RemoveEffects
            )
        }

        override fun hashStruct(): HashHolder {
            return CRC32CHasher.of {
                list("effects", effects, CRC32CHasher::ofRegistryEntry)
            }
        }
    }

    class ClearAllEffects : ConsumeEffect {

        override fun getCodec(): Codec<out ConsumeEffect> {
            return CODEC
        }

        override fun getStreamCodec(): StreamCodec<out ConsumeEffect> {
            return STREAM_CODEC
        }

        companion object {
            const val ID = 3
            private val INSTANCE = ClearAllEffects()

            val CODEC = StructCodec.of<ClearAllEffects> { INSTANCE }
            val STREAM_CODEC = StreamCodec.of { INSTANCE }
        }

        override fun hashStruct(): HashHolder {
            return StaticHash(CRC32CHasher.EMPTY_MAP)
        }
    }

    data class TeleportRandomly(val diameter: Float = DEFAULT_DIAMETER) : ConsumeEffect {

        override fun getCodec(): Codec<out ConsumeEffect> {
            return CODEC
        }

        override fun getStreamCodec(): StreamCodec<out ConsumeEffect> {
            return STREAM_CODEC
        }

        companion object {
            const val DEFAULT_DIAMETER = 16.0f
            const val ID = 3

            val STREAM_CODEC = StreamCodec.of(
                StreamCodec.FLOAT, TeleportRandomly::diameter,
                ::TeleportRandomly
            )

            val CODEC = StructCodec.of(
                "diameter", Codec.FLOAT, TeleportRandomly::diameter,
                ::TeleportRandomly
            )
        }

        override fun hashStruct(): HashHolder {
            return CRC32CHasher.of {
                default("diameter", DEFAULT_DIAMETER, diameter, CRC32CHasher::ofFloat)
            }
        }
    }

    data class PlaySound(val sound: String) : ConsumeEffect {

        override fun getCodec(): Codec<out ConsumeEffect> {
            return CODEC
        }

        override fun getStreamCodec(): StreamCodec<out ConsumeEffect> {
            return STREAM_CODEC
        }

        companion object {
            const val ID = 4

        }

        override fun hashStruct(): HashHolder {
            return CRC32CHasher.of {
                static("sound", CustomSoundEvent(sound).hashStruct().getHashed())
            }
        }
    }
}