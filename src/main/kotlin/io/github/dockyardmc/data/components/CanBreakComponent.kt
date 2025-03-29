package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.predicate.BlockPredicates
import io.netty.buffer.ByteBuf

class CanBreakComponent(val predicates: BlockPredicates): DataComponent() {

    override fun write(buffer: ByteBuf) {
        predicates.write(buffer)
    }

    companion object : NetworkReadable<CanBreakComponent> {
        override fun read(buffer: ByteBuf): CanBreakComponent {
            return CanBreakComponent(BlockPredicates.read(buffer))
        }
    }
}