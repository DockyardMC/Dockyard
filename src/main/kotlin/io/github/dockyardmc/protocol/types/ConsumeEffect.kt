package io.github.dockyardmc.protocol.types

import io.github.dockyardmc.codec.RegistryCodec
import io.github.dockyardmc.codec.transcoder.CRC32CTranscoder
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.effects.AppliedPotionEffect
import io.github.dockyardmc.protocol.DataComponentHashable
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.registry.registries.PotionEffect
import io.github.dockyardmc.registry.registries.PotionEffectRegistry
import io.github.dockyardmc.sounds.SoundEvent
import io.github.dockyardmc.tide.codec.Codec
import io.github.dockyardmc.tide.codec.StructCodec
import io.github.dockyardmc.tide.stream.StreamCodec
import io.netty.buffer.ByteBuf

sealed interface ConsumeEffect : DataComponentHashable {

    enum class Type(val key: String) {
        APPLY_EFFECTS("minecraft:apply_effects"),
        REMOVE_EFFECTS("minecraft:remove_effects"),
        CLEAR_ALL_EFFECTS("minecraft:clear_all_effects"),
        TELEPORT_RANDOMLY("minecraft:teleport_randomly"),
        PLAY_SOUND("minecraft:play_sound");

        companion object {
            val CODEC = Codec.STRING.transform<Type>({ from -> Type.entries.first { it.key == from } }, { to -> to.key })
        }
    }

    override fun hashStruct(): HashHolder {
        return StaticHash(CODEC.encode(CRC32CTranscoder, this))
    }

    companion object : NetworkReadable<ConsumeEffect> {

        val STREAM_CODEC = StreamCodec.enum<Type>().union(
            { type ->
                when (type) {
                    Type.APPLY_EFFECTS -> ApplyEffects.STREAM_CODEC
                    Type.REMOVE_EFFECTS -> RemoveEffects.STREAM_CODEC
                    Type.CLEAR_ALL_EFFECTS -> ClearAllEffects.STREAM_CODEC
                    Type.TELEPORT_RANDOMLY -> TeleportRandomly.STREAM_CODEC
                    Type.PLAY_SOUND -> PlaySound.STREAM_CODEC
                }
            },
            { effect ->
                when (effect) {
                    is ApplyEffects -> Type.APPLY_EFFECTS
                    is ClearAllEffects -> Type.CLEAR_ALL_EFFECTS
                    is PlaySound -> Type.PLAY_SOUND
                    is RemoveEffects -> Type.REMOVE_EFFECTS
                    is TeleportRandomly -> Type.TELEPORT_RANDOMLY
                }
            }
        )

        val CODEC = Type.CODEC.union(
            { type ->
                when (type) {
                    Type.APPLY_EFFECTS -> ApplyEffects.CODEC
                    Type.REMOVE_EFFECTS -> RemoveEffects.CODEC
                    Type.CLEAR_ALL_EFFECTS -> ClearAllEffects.CODEC
                    Type.TELEPORT_RANDOMLY -> TeleportRandomly.CODEC
                    Type.PLAY_SOUND -> PlaySound.CODEC
                }
            },
            { effect ->
                when (effect) {
                    is ApplyEffects -> Type.APPLY_EFFECTS
                    is ClearAllEffects -> Type.CLEAR_ALL_EFFECTS
                    is PlaySound -> Type.PLAY_SOUND
                    is RemoveEffects -> Type.REMOVE_EFFECTS
                    is TeleportRandomly -> Type.TELEPORT_RANDOMLY
                }
            }
        )

        override fun read(buffer: ByteBuf): ConsumeEffect {
            return STREAM_CODEC.read(buffer)
        }
    }

    data class ApplyEffects(val effects: List<AppliedPotionEffect>, val probability: Float = 1f) : ConsumeEffect {

        init {
            require(probability in 0f..1f) { "Probability must be between 0f and 1f" }
        }

        companion object {
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
    }

    data class RemoveEffects(val effects: List<PotionEffect>) : ConsumeEffect {

        companion object {
            val STREAM_CODEC = StreamCodec.of(
                RegistryCodec.stream(PotionEffectRegistry).list(), RemoveEffects::effects,
                ::RemoveEffects
            )

            val CODEC = StructCodec.of(
                "effects", RegistryCodec.codec(PotionEffectRegistry).list(), RemoveEffects::effects,
                ::RemoveEffects
            )
        }
    }

    class ClearAllEffects : ConsumeEffect {

        companion object {
//            private val INSTANCE get() = ClearAllEffects()

            val CODEC = StructCodec.of(::ClearAllEffects)
            val STREAM_CODEC = StreamCodec.of(::ClearAllEffects)
        }
    }

    data class TeleportRandomly(val diameter: Float = DEFAULT_DIAMETER) : ConsumeEffect {

        companion object {
            const val DEFAULT_DIAMETER = 16.0f

            val STREAM_CODEC = StreamCodec.of(
                StreamCodec.FLOAT, TeleportRandomly::diameter,
                ::TeleportRandomly
            )

            val CODEC = StructCodec.of(
                "diameter", Codec.FLOAT.default(DEFAULT_DIAMETER), TeleportRandomly::diameter,
                ::TeleportRandomly
            )
        }
    }

    data class PlaySound(val sound: SoundEvent) : ConsumeEffect {

        companion object {
            val CODEC = StructCodec.of(
                "sound", SoundEvent.CODEC, PlaySound::sound,
                ::PlaySound
            )

            val STREAM_CODEC = StreamCodec.of(
                SoundEvent.STREAM_CODEC, PlaySound::sound,
                ::PlaySound
            )
        }
    }
}