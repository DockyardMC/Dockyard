package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readEnum
import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

data class LlamaVariantComponent(val variant: Variant) : DataComponent() {
    override fun getCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeEnum(variant)
    }

    companion object : NetworkReadable<LlamaVariantComponent> {
        override fun read(buffer: ByteBuf): LlamaVariantComponent {
            return LlamaVariantComponent(buffer.readEnum())
        }
    }

    enum class Variant {
        CREAMY,
        WHITE,
        BROWN,
        GAY
    }
}