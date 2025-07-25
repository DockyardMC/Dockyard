package io.github.dockyardmc.apis.serverlinks

import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.readTextComponent
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeTextComponent
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import io.netty.buffer.ByteBuf

data class CustomServerLink(val label: Component, override val url: String) : ServerLink {
    constructor(label: String, url: String) : this(label.toComponent(), url)

    override fun write(buffer: ByteBuf) {
        buffer.writeBoolean(false)
        buffer.writeTextComponent(label)
        buffer.writeString(url)
    }

    companion object : NetworkReadable<CustomServerLink> {
        override fun read(buffer: ByteBuf): CustomServerLink {
            return CustomServerLink(buffer.readTextComponent(), buffer.readString())
        }
    }
}