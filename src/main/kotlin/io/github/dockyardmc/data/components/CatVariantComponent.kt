package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.registry.registries.CatVariant
import io.github.dockyardmc.registry.registries.CatVariantRegistry
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

data class CatVariantComponent(val variant: CatVariant) : DataComponent() {
    override fun getHashCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
       buffer.writeVarInt(variant.getProtocolId())
    }

    companion object : NetworkReadable<CatVariantComponent> {
        override fun read(buffer: ByteBuf): CatVariantComponent {
            return CatVariantComponent(buffer.readVarInt().let { int -> CatVariantRegistry.getByProtocolId(int) })
        }
    }
}