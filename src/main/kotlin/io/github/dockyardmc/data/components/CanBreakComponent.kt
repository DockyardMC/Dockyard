package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.predicate.BlockPredicates
import io.netty.buffer.ByteBuf

class CanBreakComponent(val predicates: BlockPredicates) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        predicates.write(buffer)
    }

    //TODO(1.21.5): wait for minestom to do this so I can yoink it I don't understand block predicates
    override fun hashStruct(): HashHolder {
        return unsupported(this::class)
    }

    companion object : NetworkReadable<CanBreakComponent> {
        override fun read(buffer: ByteBuf): CanBreakComponent {
            return CanBreakComponent(BlockPredicates.read(buffer))
        }
    }
}