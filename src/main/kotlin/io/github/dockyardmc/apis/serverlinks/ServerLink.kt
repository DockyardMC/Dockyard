package io.github.dockyardmc.apis.serverlinks

import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.netty.buffer.ByteBuf

sealed interface ServerLink: NetworkWritable {
    val url: String

    companion object: NetworkReadable<ServerLink> {
        override fun read(buffer: ByteBuf): ServerLink {
            val isDefault = buffer.readBoolean()
            return if(isDefault) DefaultServerLink.read(buffer) else CustomServerLink.read(buffer)
        }
    }
}