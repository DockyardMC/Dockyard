package io.github.dockyardmc.registry.dummy

import io.github.dockyardmc.extentions.readTextComponent
import io.github.dockyardmc.extentions.writeTextComponent
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.sounds.SoundEvent
import io.netty.buffer.ByteBuf

data class DummyInstrument(val soundEvent: SoundEvent, val useDuration: Float, val range: Float, val description: Component) : NetworkWritable {

    override fun write(buffer: ByteBuf) {
        SoundEvent.STREAM_CODEC.write(buffer, soundEvent)
        buffer.writeFloat(useDuration)
        buffer.writeFloat(range)
        buffer.writeTextComponent(description)
    }

    companion object : NetworkReadable<DummyInstrument> {
        override fun read(buffer: ByteBuf): DummyInstrument {
            return DummyInstrument(
                SoundEvent.STREAM_CODEC.read(buffer),
                buffer.readFloat(),
                buffer.readFloat(),
                buffer.readTextComponent()
            )
        }
    }
}