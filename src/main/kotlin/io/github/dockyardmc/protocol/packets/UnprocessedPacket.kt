package io.github.dockyardmc.protocol.packets

import io.netty.buffer.ByteBuf

class UnprocessedPacket(
    val id: Int,
    val size: Int,
    val data: ByteBuf,
)