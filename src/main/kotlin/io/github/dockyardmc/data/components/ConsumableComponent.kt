package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readConsumeEffects
import io.github.dockyardmc.extentions.readEnum
import io.github.dockyardmc.extentions.writeConsumeEffects
import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.item.ConsumableAnimation
import io.github.dockyardmc.item.ConsumeEffect
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.sounds.CustomSoundEvent
import io.github.dockyardmc.sounds.SoundEvent
import io.netty.buffer.ByteBuf

class ConsumableComponent(
    val consumeSeconds: Float,
    val animation: ConsumableAnimation,
    val sound: String,
    val hasParticles: Boolean,
    val effects: List<ConsumeEffect>
) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeFloat(consumeSeconds)
        buffer.writeEnum(animation)
        CustomSoundEvent(sound).write(buffer)
        buffer.writeBoolean(hasParticles)
        buffer.writeConsumeEffects(effects)
    }

    companion object : NetworkReadable<ConsumableComponent> {
        override fun read(buffer: ByteBuf): ConsumableComponent {
            return ConsumableComponent(
                buffer.readFloat(),
                buffer.readEnum(),
                SoundEvent.read(buffer).identifier,
                buffer.readBoolean(),
                buffer.readConsumeEffects()
            )
        }
    }

}