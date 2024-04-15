package io.github.dockyardmc.protocol.packets

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

open class ClientboundPacket {

    val data: ByteBuf = Unpooled.buffer()

    fun asByteBuf(): ByteBuf {
        return data
    }
}