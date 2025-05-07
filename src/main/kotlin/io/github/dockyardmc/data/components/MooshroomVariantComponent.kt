package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readEnum
import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class MooshroomVariantComponent(val variant: Variant) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeEnum(variant)
    }

    companion object : NetworkReadable<MooshroomVariantComponent> {
        override fun read(buffer: ByteBuf): MooshroomVariantComponent {
            return MooshroomVariantComponent(buffer.readEnum())
        }
    }

    enum class Variant {
        RED,
        BROWN
    }
}