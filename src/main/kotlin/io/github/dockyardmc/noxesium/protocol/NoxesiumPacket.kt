package io.github.dockyardmc.noxesium.protocol

import io.github.dockyardmc.tide.Codec

interface NoxesiumPacket {
    fun getStreamCodec(): Codec<out NoxesiumPacket>
}