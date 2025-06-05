package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.entity.ParrotVariant
import io.github.dockyardmc.extentions.readEnum
import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

data class ParrotVariantComponent(val parrotColor: ParrotVariant) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeEnum(parrotColor)
    }

    companion object : NetworkReadable<ParrotVariantComponent> {
        override fun read(buffer: ByteBuf): ParrotVariantComponent {
            return ParrotVariantComponent(buffer.readEnum())
        }
    }
}