package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.registry.dummy.DummyInstrument
import io.netty.buffer.ByteBuf

class InstrumentComponent(val instrument: DummyInstrument) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        instrument.write(buffer)
    }

    companion object : NetworkReadable<InstrumentComponent> {
        override fun read(buffer: ByteBuf): InstrumentComponent {
            return InstrumentComponent(DummyInstrument.read(buffer))
        }
    }

}