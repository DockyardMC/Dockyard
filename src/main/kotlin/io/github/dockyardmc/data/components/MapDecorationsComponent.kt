package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.protocol.DataComponentHashable
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.types.readMap
import io.github.dockyardmc.protocol.types.writeMap
import io.netty.buffer.ByteBuf

class MapDecorationsComponent(val decorations: Map<String, Decoration>) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeMap(decorations, ByteBuf::writeString, Decoration::write)
    }

    override fun hashStruct(): HashHolder {
        return StaticHash(CRC32CHasher.ofMap(decorations.mapKeys { map -> CRC32CHasher.ofString(map.key) }.mapValues { map -> map.value.hashStruct().getHashed() }))
    }

    companion object : NetworkReadable<MapDecorationsComponent> {
        override fun read(buffer: ByteBuf): MapDecorationsComponent {
            return MapDecorationsComponent(buffer.readMap(ByteBuf::readString, Decoration::read))
        }
    }

    data class Decoration(val type: String, val x: Double, val z: Double, val rotation: Float) : NetworkWritable, DataComponentHashable {

        override fun hashStruct(): HashHolder {
            return CRC32CHasher.of {
                static("type", CRC32CHasher.ofString(type))
                static("x", CRC32CHasher.ofDouble(x))
                static("z", CRC32CHasher.ofDouble(z))
                static("rotation", CRC32CHasher.ofFloat(rotation))
            }
        }

        override fun write(buffer: ByteBuf) {
            buffer.writeString(type)
            buffer.writeDouble(x)
            buffer.writeDouble(z)
            buffer.writeFloat(rotation)
        }

        companion object : NetworkReadable<Decoration> {
            override fun read(buffer: ByteBuf): Decoration {
                return Decoration(
                    buffer.readString(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readFloat(),
                )
            }
        }
    }
}