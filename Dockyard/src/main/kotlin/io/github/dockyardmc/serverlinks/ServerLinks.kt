package io.github.dockyardmc.serverlinks

import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.netty.buffer.ByteBuf

object ServerLinks {
    val links: MutableList<ServerLink> = mutableListOf()
}


fun ByteBuf.writeServerLinks(serverLinks: MutableList<ServerLink>) {
    this.writeVarInt(serverLinks.size)

    serverLinks.forEach {
        when(it) {
            is DefaultServerLink -> {
                this.writeBoolean(true)
                this.writeVarInt(it.type.ordinal)
            }
            is CustomServerLink -> {
                this.writeBoolean(false)
                this.writeNBT(it.label.toNBT())
            }
        }
        this.writeString(it.url)
    }
}