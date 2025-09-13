package io.github.dockyardmc.data.components

import io.github.dockyardmc.codec.transcoder.CRC32CTranscoder
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.ConsumeEffect
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.sounds.BuiltinSoundEvent
import io.github.dockyardmc.sounds.SoundEvent
import io.github.dockyardmc.tide.codec.Codec
import io.github.dockyardmc.tide.codec.StructCodec
import io.github.dockyardmc.tide.stream.StreamCodec
import io.netty.buffer.ByteBuf

data class ConsumableComponent(
    val consumeSeconds: Float,
    val animation: Animation,
    val sound: SoundEvent,
    val hasParticles: Boolean,
    val effects: List<ConsumeEffect>
) : DataComponent() {

    companion object : NetworkReadable<ConsumableComponent> {

        const val CONSUME_SECONDS_DEFAULT = 1.6f
        const val HAS_CONSUME_PARTICLES_DEFAULT = true
        val ANIMATION_DEFAULT = Animation.EAT
        val SOUND_DEFAULT = BuiltinSoundEvent(Sounds.ENTITY_GENERIC_EAT)
        val CONSUME_EFFECTS_DEFAULT = listOf<ConsumeEffect>()

        val CODEC = StructCodec.of(
            "consume_seconds", Codec.FLOAT.default(CONSUME_SECONDS_DEFAULT), ConsumableComponent::consumeSeconds,
            "animation", Codec.enum<Animation>().default(ANIMATION_DEFAULT), ConsumableComponent::animation,
            "sound", SoundEvent.CODEC.default(SOUND_DEFAULT), ConsumableComponent::sound,
            "has_consume_particles", Codec.BOOLEAN.default(HAS_CONSUME_PARTICLES_DEFAULT), ConsumableComponent::hasParticles,
            "on_consume_effects", ConsumeEffect.CODEC.list().default(CONSUME_EFFECTS_DEFAULT), ConsumableComponent::effects,
            ::ConsumableComponent
        )

        val STREAM_CODEC = StreamCodec.of(
            StreamCodec.FLOAT, ConsumableComponent::consumeSeconds,
            StreamCodec.enum(), ConsumableComponent::animation,
            SoundEvent.STREAM_CODEC, ConsumableComponent::sound,
            StreamCodec.BOOLEAN, ConsumableComponent::hasParticles,
            ConsumeEffect.STREAM_CODEC.list(), ConsumableComponent::effects,
            ::ConsumableComponent
        )

        override fun read(buffer: ByteBuf): ConsumableComponent {
            return STREAM_CODEC.read(buffer)
        }
    }

    override fun hashStruct(): HashHolder {
        return StaticHash(CODEC.encode(CRC32CTranscoder, this))
    }

    override fun write(buffer: ByteBuf) {
        STREAM_CODEC.write(buffer, this)
    }


    enum class Animation(val decreasesAmount: Boolean) {
        NONE(false),
        EAT(true),
        DRINK(true),
        BLOCK(false),
        BOW(false),
        SPEAR(false),
        CROSSBOW(false),
        SPYGLASS(false),
        TOOT_HORN(false),
        BRUSH(false),
        BUNDLE(false);
    }
}