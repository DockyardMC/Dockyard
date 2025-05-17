package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.extentions.readEnum
import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.ConsumeEffect
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.sounds.BuiltinSoundEvent
import io.github.dockyardmc.sounds.CustomSoundEvent
import io.github.dockyardmc.sounds.SoundEvent
import io.netty.buffer.ByteBuf

class ConsumableComponent(
    val consumeSeconds: Float,
    val animation: Animation,
    val sound: String,
    val hasParticles: Boolean,
    val effects: List<ConsumeEffect>
) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeFloat(consumeSeconds)
        buffer.writeEnum(animation)
        CustomSoundEvent(sound).write(buffer)
        buffer.writeBoolean(hasParticles)
        buffer.writeList(effects, ConsumeEffect::write)
    }

    override fun hashStruct(): HashHolder {
        return CRC32CHasher.of {
            default("consume_seconds", CONSUME_SECONDS_DEFAULT, consumeSeconds, CRC32CHasher::ofFloat)
            default("animation", Animation.EAT, animation, CRC32CHasher::ofEnum)
            defaultStruct("sound", BuiltinSoundEvent.of(Sounds.ENTITY_GENERIC_EAT), CustomSoundEvent(sound), SoundEvent::hashStruct)
            default<Boolean>("has_consume_particles", HAS_CONSUME_PARTICLES_DEFAULT, hasParticles, CRC32CHasher::ofBoolean)
            defaultStructList("on_consume_effects", listOf(), effects, ConsumeEffect::hashStruct)
//            static("on_consume_effects", CRC32CHasher.EMPTY)
        }
    }

    companion object : NetworkReadable<ConsumableComponent> {

        const val CONSUME_SECONDS_DEFAULT = 1.6f
        const val HAS_CONSUME_PARTICLES_DEFAULT = true

        override fun read(buffer: ByteBuf): ConsumableComponent {
            return ConsumableComponent(
                buffer.readFloat(),
                buffer.readEnum(),
                SoundEvent.read(buffer).identifier,
                buffer.readBoolean(),
                //buffer.readList(ConsumeEffect::read)
                emptyList() //TODO(1.21.5): Consume effects hashing
            )
        }
    }

    enum class Animation {
        NONE,
        EAT,
        DRINK,
        BLOCK,
        BOW,
        SPEAR,
        CROSSBOW,
        SPYGLASS,
        TOOT_HORN,
        BRUSH,
        BUNDLE;
    }
}