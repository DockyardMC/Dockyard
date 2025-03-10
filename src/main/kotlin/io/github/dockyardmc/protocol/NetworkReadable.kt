package io.github.dockyardmc.protocol

import io.netty.buffer.ByteBuf

interface NetworkReadable <T> {
    fun read(buffer: ByteBuf): T
}