package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.registry.dummy.DummyInstrument
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class InstrumentComponent(val instrument: DummyInstrument) : DataComponent() {
    override fun getHashCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
        instrument.write(buffer)
    }

    companion object : NetworkReadable<InstrumentComponent> {
        override fun read(buffer: ByteBuf): InstrumentComponent {
            return InstrumentComponent(DummyInstrument.read(buffer))
        }
    }

}