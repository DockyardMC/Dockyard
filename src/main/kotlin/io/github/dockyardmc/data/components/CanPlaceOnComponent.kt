package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.predicate.BlockPredicates
import io.netty.buffer.ByteBuf

class CanPlaceOnComponent(val predicates: BlockPredicates): DataComponent() {

    override fun write(buffer: ByteBuf) {
        predicates.write(buffer)
    }

    companion object : NetworkReadable<CanPlaceOnComponent> {
        override fun read(buffer: ByteBuf): CanPlaceOnComponent {
            return CanPlaceOnComponent(BlockPredicates.read(buffer))
        }
    }
}