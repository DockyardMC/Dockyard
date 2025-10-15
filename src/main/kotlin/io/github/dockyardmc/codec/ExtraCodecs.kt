package io.github.dockyardmc.codec

import io.github.dockyardmc.extentions.read
import io.github.dockyardmc.extentions.writeColor
import io.github.dockyardmc.scroll.CustomColor
import io.github.dockyardmc.tide.stream.StreamCodec
import io.netty.buffer.ByteBuf

object ExtraCodecs {
    fun <T> fieldWriter(innerCodec: StreamCodec<T>): StreamCodec<T> {
        return object : StreamCodec<T> {

            override fun write(buffer: ByteBuf, value: T) {
                innerCodec.write(buffer, value)
            }

            override fun read(buffer: ByteBuf): T {
                return innerCodec.read(buffer)
            }

        }
    }

    val CUSTOM_COLOR_STREAM = object : StreamCodec<CustomColor> {

        override fun write(buffer: ByteBuf, value: CustomColor) {
            buffer.writeColor(value)
        }

        override fun read(buffer: ByteBuf): CustomColor {
            return CustomColor.read(buffer)
        }

    }
}