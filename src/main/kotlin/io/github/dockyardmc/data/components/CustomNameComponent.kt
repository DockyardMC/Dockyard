package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readTextComponent
import io.github.dockyardmc.extentions.writeTextComponent
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class CustomNameComponent(val component: Component): DataComponent() {

    constructor(name: String): this(name.toComponent())

    override fun getCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

//    override fun hash(hasher: Hasher): Int {
////        hasher.put
//    }

    override fun write(buffer: ByteBuf) {
        buffer.writeTextComponent(component)
    }

    companion object: NetworkReadable<CustomNameComponent> {
        override fun read(buffer: ByteBuf): CustomNameComponent {
            return CustomNameComponent(buffer.readTextComponent())
        }
    }
}