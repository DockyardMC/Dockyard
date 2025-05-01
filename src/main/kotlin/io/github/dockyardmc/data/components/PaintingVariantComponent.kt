package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.registry.registries.PaintingVariant
import io.github.dockyardmc.registry.registries.PaintingVariantRegistry
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

data class PaintingVariantComponent(val variant: PaintingVariant) : DataComponent() {
    override fun getCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(variant.getProtocolId())
    }

    companion object : NetworkReadable<PaintingVariantComponent> {
        override fun read(buffer: ByteBuf): PaintingVariantComponent {
            return PaintingVariantComponent(buffer.readVarInt().let { int -> PaintingVariantRegistry.getByProtocolId(int) })
        }
    }
}