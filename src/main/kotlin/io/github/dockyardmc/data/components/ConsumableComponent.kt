package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.extentions.readEnum
import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.ConsumeEffect
import io.github.dockyardmc.protocol.types.readList
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.sounds.BuiltinSoundEvent
import io.github.dockyardmc.sounds.CustomSoundEvent
import io.github.dockyardmc.sounds.SoundEvent
import io.github.dockyardmc.tide.codec.Codec
import io.github.dockyardmc.tide.codec.StructCodec
import io.netty.buffer.ByteBuf

data class ConsumableComponent(
    val consumeSeconds: Float,
    val animation: Animation,
    val sound: String,
    val hasParticles: Boolean,
    val effects: List<ConsumeEffect>
) : DataComponent() {

    companion object : NetworkReadable<ConsumableComponent> {

        const val CONSUME_SECONDS_DEFAULT = 1.6f
        const val HAS_CONSUME_PARTICLES_DEFAULT = true
        val ANIMATION_DEFAULT = Animation.EAT
        val SOUND_DEFAULT = BuiltinSoundEvent.of(Sounds.ENTITY_GENERIC_EAT)
        val CONSUME_EFFECTS_DEFAULT = listOf<ConsumeEffect>()

        val CODEC = StructCodec.of(
            "consume_seconds", Codec.FLOAT.default(CONSUME_SECONDS_DEFAULT), ConsumableComponent::consumeSeconds,
            "animation", Codec.enum<Animation>().default(ANIMATION_DEFAULT), ConsumableComponent::animation,
            "sound", Codec.STRING, ConsumableComponent::sound, //WRONG
            "has_consume_particles", Codec.BOOLEAN.default(HAS_CONSUME_PARTICLES_DEFAULT), ConsumableComponent::hasParticles,
            "on_consume_effects", ConsumeEffect.CODEC.list().default(CONSUME_EFFECTS_DEFAULT), ConsumableComponent::effects,
            ::ConsumableComponent
        )

        override fun read(buffer: ByteBuf): ConsumableComponent {
            return ConsumableComponent(
                buffer.readFloat(),
                buffer.readEnum(),
                SoundEvent.read(buffer).identifier,
                buffer.readBoolean(),
                buffer.readList(ConsumeEffect::read)
            )
        }
    }

    override fun hashStruct(): HashHolder {
        return CRC32CHasher.of {
            default("consume_seconds", CONSUME_SECONDS_DEFAULT, consumeSeconds, CRC32CHasher::ofFloat)
            default("animation",ANIMATION_DEFAULT, animation, CRC32CHasher::ofEnum)
            defaultStruct("sound", BuiltinSoundEvent.of(Sounds.ENTITY_GENERIC_EAT), CustomSoundEvent(sound), SoundEvent::hashStruct)
            default<Boolean>("has_consume_particles", HAS_CONSUME_PARTICLES_DEFAULT, hasParticles, CRC32CHasher::ofBoolean)
            defaultStructList("on_consume_effects", listOf(), effects, ConsumeEffect::hashStruct)
        }
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeFloat(consumeSeconds)
        buffer.writeEnum(animation)
        CustomSoundEvent(sound).write(buffer)
        buffer.writeBoolean(hasParticles)
        buffer.writeList(effects, ConsumeEffect::write)
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