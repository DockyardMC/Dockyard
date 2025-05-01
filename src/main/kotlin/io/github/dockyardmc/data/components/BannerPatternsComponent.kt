package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.readEnum
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.types.DyeColor
import io.github.dockyardmc.protocol.types.readList
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.registry.registries.BannerPattern
import io.github.dockyardmc.registry.registries.BannerPatternRegistry
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class BannerPatternsComponent(val layers: List<Layer>) : DataComponent() {
    override fun getCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeList(layers, Layer::write)
    }

    companion object : NetworkReadable<BannerPatternsComponent> {
        override fun read(buffer: ByteBuf): BannerPatternsComponent {
            return BannerPatternsComponent(buffer.readList(Layer::read))
        }
    }

    data class Layer(val pattern: BannerPattern, val color: DyeColor) : NetworkWritable {

        override fun write(buffer: ByteBuf) {
            buffer.writeVarInt(pattern.getProtocolId())
            buffer.writeEnum(color)
        }

        companion object : NetworkReadable<Layer> {
            override fun read(buffer: ByteBuf): Layer {
                return Layer(buffer.readVarInt().let { int -> BannerPatternRegistry.getByProtocolId(int) }, buffer.readEnum())
            }
        }
    }
}