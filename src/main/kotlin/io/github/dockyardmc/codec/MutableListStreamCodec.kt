package io.github.dockyardmc.codec

import io.github.dockyardmc.tide.stream.StreamCodec
import io.netty.buffer.ByteBuf

class MutableListStreamCodec<T>(val inner: StreamCodec<T>) : StreamCodec<MutableList<T>> {

    override fun write(buffer: ByteBuf, value: MutableList<T>) {
        StreamCodec.VAR_INT.write(buffer, value.size)
        value.forEach { item ->
            inner.write(buffer, item)
        }
    }

    override fun read(buffer: ByteBuf): MutableList<T> {
        val size = StreamCodec.VAR_INT.read(buffer)
        val list = mutableListOf<T>()
        for (i in 0 until size) {
            list.add(inner.read(buffer))
        }
        return list
    }
}