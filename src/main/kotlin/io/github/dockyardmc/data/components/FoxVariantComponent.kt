package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readEnum
import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

class FoxVariantComponent(val variant: Variant) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeEnum(variant)
    }

    companion object : NetworkReadable<FoxVariantComponent> {
        override fun read(buffer: ByteBuf): FoxVariantComponent {
            return FoxVariantComponent(buffer.readEnum())
        }
    }

    enum class Variant {
        RED,
        SNOW
    }
}