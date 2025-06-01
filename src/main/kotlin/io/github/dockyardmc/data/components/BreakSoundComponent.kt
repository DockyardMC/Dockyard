package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.sounds.BuiltinSoundEvent
import io.github.dockyardmc.sounds.SoundEvent
import io.netty.buffer.ByteBuf

data class BreakSoundComponent(val sound: String) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        BuiltinSoundEvent.of(sound).write(buffer)
    }

    override fun hashStruct(): HashHolder {
        return StaticHash(BuiltinSoundEvent.of(sound).hashStruct().getHashed())
    }

    companion object : NetworkReadable<BreakSoundComponent> {
        override fun read(buffer: ByteBuf): BreakSoundComponent {
            return BreakSoundComponent(SoundEvent.read(buffer).identifier)
        }
    }
}