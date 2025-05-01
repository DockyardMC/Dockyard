package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.fromRGBInt
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.toRgbInt
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.readList
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.scroll.CustomColor
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class CustomModelDataComponent(val floats: List<Float>, val flags: List<Boolean>, val strings: List<String>, val colors: List<CustomColor>) : DataComponent() {
    override fun getHashCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeList(floats, ByteBuf::writeFloat)
        buffer.writeList(flags, ByteBuf::writeBoolean)
        buffer.writeList(strings, ByteBuf::writeString)
        buffer.writeList(colors.map { color -> color.toRgbInt() }, ByteBuf::writeInt)
    }

    companion object : NetworkReadable<CustomModelDataComponent> {
        override fun read(buffer: ByteBuf): CustomModelDataComponent {
            val floats = buffer.readList(ByteBuf::readFloat)
            val flags = buffer.readList(ByteBuf::readBoolean)
            val strings = buffer.readList(ByteBuf::readString)
            val colors = buffer.readList(ByteBuf::readInt).map { int -> CustomColor.fromRGBInt(int) }

            return CustomModelDataComponent(floats, flags, strings, colors)
        }
    }
}