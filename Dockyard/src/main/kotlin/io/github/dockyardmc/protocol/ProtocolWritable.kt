package io.github.dockyardmc.protocol

import io.netty.buffer.ByteBuf

interface ProtocolWritable {

    fun write(buffer: ByteBuf)

}