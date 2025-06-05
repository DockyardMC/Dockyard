package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.extentions.readTextComponent
import io.github.dockyardmc.extentions.writeTextComponent
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import io.netty.buffer.ByteBuf

class CustomNameComponent(val component: Component) : DataComponent() {

    constructor(name: String) : this(name.toComponent())

    override fun write(buffer: ByteBuf) {
        buffer.writeTextComponent(component)
    }

    override fun hashStruct(): HashHolder {
        return StaticHash(CRC32CHasher.ofNbt(component.toNBT()))
    }

    companion object : NetworkReadable<CustomNameComponent> {
        override fun read(buffer: ByteBuf): CustomNameComponent {
            return CustomNameComponent(buffer.readTextComponent())
        }
    }
}