package io.github.dockyardmc.protocol.writers

import io.netty.buffer.ByteBuf
import java.util.*

fun ByteBuf.readUUID(): UUID {
    val most = this.readLong()
    val least = this.readLong()
    return UUID(most, least)
}

fun ByteBuf.writeUUID(uuid: UUID) {
    this.writeLong(uuid.mostSignificantBits)
    this.writeLong(uuid.leastSignificantBits)
}