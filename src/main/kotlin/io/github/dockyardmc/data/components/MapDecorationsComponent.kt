package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.types.readMap
import io.github.dockyardmc.protocol.types.writeMap
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class MapDecorationsComponent(val decorations: Map<String, Decoration>) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeMap(decorations, ByteBuf::writeString, Decoration::write)
    }

    companion object : NetworkReadable<MapDecorationsComponent> {
        override fun read(buffer: ByteBuf): MapDecorationsComponent {
            return MapDecorationsComponent(buffer.readMap(ByteBuf::readString, Decoration::read))
        }
    }

    data class Decoration(val type: String, val x: Double, val z: Double, val rotation: Float) : NetworkWritable {

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