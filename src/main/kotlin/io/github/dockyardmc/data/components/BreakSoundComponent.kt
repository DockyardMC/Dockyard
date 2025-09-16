package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.sounds.SoundEvent
import io.netty.buffer.ByteBuf

data class BreakSoundComponent(val sound: SoundEvent) : DataComponent() {

    companion object : NetworkReadable<BreakSoundComponent> {
        val CODEC = SoundEvent.CODEC
        val STREAM_CODEC = SoundEvent.STREAM_CODEC

        override fun read(buffer: ByteBuf): BreakSoundComponent {
            return BreakSoundComponent(STREAM_CODEC.read(buffer))
        }
    }

    override fun write(buffer: ByteBuf) {
        STREAM_CODEC.write(buffer, sound)
    }

    override fun hashStruct(): HashHolder {
        return StaticHash(sound.hashStruct().getHashed())
    }
}