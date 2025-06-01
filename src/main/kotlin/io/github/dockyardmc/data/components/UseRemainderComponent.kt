package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

class UseRemainderComponent(val remained: ItemStack) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        remained.write(buffer)
    }

    override fun hashStruct(): HashHolder {
        return StaticHash(remained.hashStruct().getHashed())
    }

    companion object : NetworkReadable<UseRemainderComponent> {
        override fun read(buffer: ByteBuf): UseRemainderComponent {
            return UseRemainderComponent(ItemStack.read(buffer))
        }
    }
}