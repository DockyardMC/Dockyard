package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readEnum
import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class RabbitVariantComponent(val variant: Variant) : DataComponent() {
    override fun getHashCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeEnum(variant)
    }

    companion object : NetworkReadable<RabbitVariantComponent> {
        override fun read(buffer: ByteBuf): RabbitVariantComponent {
            return RabbitVariantComponent(buffer.readEnum())
        }
    }

    enum class Variant {
        BROWN,
        WHITE,
        BLACK,
        BLACK_AND_WHITE,
        GOLD,
        SALT_AND_PEPPER,
        KILLER_BUNNY
    }
}