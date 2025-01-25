package io.github.dockyardmc.protocol

import io.netty.buffer.ByteBuf

interface Packet {

    fun write(buffer: ByteBuf)

}