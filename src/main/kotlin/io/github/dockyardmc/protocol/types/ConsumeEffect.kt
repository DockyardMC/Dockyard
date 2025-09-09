package io.github.dockyardmc.protocol.types

import io.github.dockyardmc.codec.RegistryCodec
import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.effects.AppliedPotionEffect
import io.github.dockyardmc.extentions.readEnum
import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.protocol.DataComponentHashable
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.registry.registries.PotionEffect
import io.github.dockyardmc.registry.registries.PotionEffectRegistry
import io.github.dockyardmc.sounds.SoundEvent
import io.github.dockyardmc.tide.codec.Codec
import io.github.dockyardmc.tide.codec.StructCodec
import io.github.dockyardmc.tide.stream.StreamCodec
import io.github.dockyardmc.tide.transcoder.Transcoder
import io.netty.buffer.ByteBuf
import kotlin.reflect.KClass

interface ConsumeEffect : DataComponentHashable {

    fun getCodec(): Codec<out ConsumeEffect>
    fun getStreamCodec(): StreamCodec<out ConsumeEffect>

    enum class Type(val key: String, val kclass: KClass<out ConsumeEffect>, val codec: Codec<out ConsumeEffect>, val streamCodec: StreamCodec<out ConsumeEffect>) {
        APPLY_EFFECTS("apply_effects", ApplyEffects::class, ApplyEffects.CODEC, ApplyEffects.STREAM_CODEC),
        REMOVE_EFFECTS("remove_effects", ApplyEffects::class, RemoveEffects.CODEC, RemoveEffects.STREAM_CODEC),
        CLEAR_ALL_EFFECTS("clear_all_effects", ApplyEffects::class, ClearAllEffects.CODEC, ClearAllEffects.STREAM_CODEC),
        TELEPORT_RANDOMLY("teleport_randomly", ApplyEffects::class, TeleportRandomly.CODEC, TeleportRandomly.STREAM_CODEC),
        PLAY_SOUND("play_sound", ApplyEffects::class, PlaySound.CODEC, PlaySound.STREAM_CODEC);

        companion object {
            fun getForClass(kclass: KClass<out ConsumeEffect>): Type {
                return entries.first { it.kclass == kclass }
            }

            val CODEC = Codec.STRING.transform<Type>({ from -> Type.entries.first { it.key == from } }, { to -> to.key })
        }
    }

    companion object : NetworkReadable<ConsumeEffect> {

        val STREAM_CODEC = object : StreamCodec<ConsumeEffect> {

            @Suppress("UNCHECKED_CAST")
            override fun write(buffer: ByteBuf, value: ConsumeEffect) {
                buffer.writeEnum(Type.getForClass(value::class))
                (value.getStreamCodec() as StreamCodec<ConsumeEffect>).write(buffer, value)
            }

            override fun read(buffer: ByteBuf): ConsumeEffect {
                val type = buffer.readEnum<Type>()
                return type.streamCodec.read(buffer)
            }
        }
        
        val CODEC = object : Codec<ConsumeEffect> {

            @Suppress("UNCHECKED_CAST")
            override fun <D> encode(transcoder: Transcoder<D>, value: ConsumeEffect): D {
                transcoder.encodeString(Type.getForClass(value::class).key)
                return (value.getCodec() as Codec<ConsumeEffect>).encode(transcoder, value)
            }

            override fun <D> decode(transcoder: Transcoder<D>, value: D): ConsumeEffect {
                val type = Type.CODEC.decode(transcoder, value)
                return type.codec.decode(transcoder, value)
            }
        }

        override fun read(buffer: ByteBuf): ConsumeEffect {
            return STREAM_CODEC.read(buffer)
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
                "probability", Codec.FLOAT.default(1f), ApplyEffects::probability,
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
                "diameter", Codec.FLOAT.default(DEFAULT_DIAMETER), TeleportRandomly::diameter,
                ::TeleportRandomly
            )
        }

        override fun hashStruct(): HashHolder {
            return CRC32CHasher.of {
                default("diameter", DEFAULT_DIAMETER, diameter, CRC32CHasher::ofFloat)
            }
        }
    }

    data class PlaySound(val sound: SoundEvent) : ConsumeEffect {

        override fun getCodec(): Codec<out ConsumeEffect> {
            return CODEC
        }

        override fun getStreamCodec(): StreamCodec<out ConsumeEffect> {
            return STREAM_CODEC
        }

        companion object {
            const val ID = 4

            val CODEC = StructCodec.of(
                "sound", SoundEvent.CODEC, PlaySound::sound,
                ::PlaySound
            )

            val STREAM_CODEC = StreamCodec.of(
                SoundEvent.STREAM_CODEC, PlaySound::sound,
                ::PlaySound
            )
        }

        override fun hashStruct(): HashHolder {
            return CRC32CHasher.of {
                static("sound", sound.hashStruct().getHashed())
            }
        }
    }
}