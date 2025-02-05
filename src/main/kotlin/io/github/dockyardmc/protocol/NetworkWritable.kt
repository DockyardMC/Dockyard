package io.github.dockyardmc.protocol

import io.netty.buffer.ByteBuf

interface NetworkWritable {

    fun write(buffer: ByteBuf)

}