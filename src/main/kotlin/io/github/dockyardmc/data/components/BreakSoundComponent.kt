package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.sounds.CustomSoundEvent
import io.github.dockyardmc.sounds.SoundEvent
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

data class BreakSoundComponent(val sound: String) : DataComponent() {
    override fun getHashCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
       CustomSoundEvent(sound).write(buffer)
    }

    companion object : NetworkReadable<BreakSoundComponent> {
        override fun read(buffer: ByteBuf): BreakSoundComponent {
            return BreakSoundComponent(SoundEvent.read(buffer).identifier)
        }
    }
}