package io.github.dockyardmc.protocol.packets

import io.netty.buffer.ByteBuf

class UnprocessedPacket(
    val size: Int,
    val data: ByteBuf,
)