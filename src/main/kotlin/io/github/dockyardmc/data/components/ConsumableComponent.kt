package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readEnum
import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.ConsumeEffect
import io.github.dockyardmc.protocol.types.readList
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.sounds.CustomSoundEvent
import io.github.dockyardmc.sounds.SoundEvent
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class ConsumableComponent(
    val consumeSeconds: Float,
    val animation: Animation,
    val sound: String,
    val hasParticles: Boolean,
    val effects: List<ConsumeEffect>
) : DataComponent() {
    override fun getHashCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeFloat(consumeSeconds)
        buffer.writeEnum(animation)
        CustomSoundEvent(sound).write(buffer)
        buffer.writeBoolean(hasParticles)
        buffer.writeList(effects, ConsumeEffect::write)
    }

    companion object : NetworkReadable<ConsumableComponent> {
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