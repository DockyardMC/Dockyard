package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.HashList
import io.github.dockyardmc.extentions.readEnum
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.DataComponentHashable
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.types.DyeColor
import io.github.dockyardmc.protocol.types.readList
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.registry.registries.BannerPattern
import io.github.dockyardmc.registry.registries.BannerPatternRegistry
import io.netty.buffer.ByteBuf

class BannerPatternsComponent(val layers: List<Layer>) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeList(layers, Layer::write)
    }

    override fun hashStruct(): HashHolder {
        return HashList(layers.map { layer -> layer.hashStruct() })
    }

    companion object : NetworkReadable<BannerPatternsComponent> {
        override fun read(buffer: ByteBuf): BannerPatternsComponent {
            return BannerPatternsComponent(buffer.readList(Layer::read))
        }
    }

    data class Layer(val pattern: BannerPattern, val color: DyeColor) : NetworkWritable, DataComponentHashable {

        override fun write(buffer: ByteBuf) {
            buffer.writeVarInt(pattern.getProtocolId())
            buffer.writeEnum(color)
        }

        companion object : NetworkReadable<Layer> {
            override fun read(buffer: ByteBuf): Layer {
                return Layer(buffer.readVarInt().let { int -> BannerPatternRegistry.getByProtocolId(int) }, buffer.readEnum())
            }
        }

        override fun hashStruct(): HashHolder {
            return CRC32CHasher.of {
                static("pattern", CRC32CHasher.ofRegistryEntry(pattern))
                static("color", CRC32CHasher.ofEnum(color))
            }
        }
    }
}