package io.github.dockyardmc.codec

import io.github.dockyardmc.tide.codec.Codec
import io.github.dockyardmc.tide.stream.StreamCodec

fun <T> StreamCodec<T>.mutableList(): MutableListStreamCodec<T> {
    return MutableListStreamCodec<T>(this)
}

fun <T> Codec<T>.mutableList(): MutableListCodec<T> {
    return MutableListCodec<T>(this)
}