package io.github.dockyardmc.codec

import io.github.dockyardmc.tide.codec.Codec
import io.github.dockyardmc.tide.transcoder.Transcoder

class MutableListCodec<T>(val inner: Codec<T>) : Codec<MutableList<T>> {

    override fun <D> encode(transcoder: Transcoder<D>, value: MutableList<T>): D {
        val encodedList = transcoder.encodeList(value.size)
        value.forEach { item ->
            encodedList.add(inner.encode(transcoder, item))
        }
        return encodedList.build()
    }

    override fun <D> decode(transcoder: Transcoder<D>, value: D): MutableList<T> {
        val listResult = transcoder.decodeList(value)
        val decodedList = mutableListOf<T>()
        listResult.forEach { item ->
            decodedList.add(inner.decode(transcoder, item))
        }
        return decodedList
    }
}