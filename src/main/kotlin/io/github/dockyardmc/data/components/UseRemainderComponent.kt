package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class UseRemainderComponent(val remained: ItemStack): DataComponent() {

    override fun write(buffer: ByteBuf) {
        remained.write(buffer)
    }

    companion object: NetworkReadable<UseRemainderComponent> {
        override fun read(buffer: ByteBuf): UseRemainderComponent {
            return UseRemainderComponent(ItemStack.read(buffer))
        }
    }
}