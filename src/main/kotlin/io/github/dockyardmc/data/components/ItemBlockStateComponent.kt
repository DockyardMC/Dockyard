package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.readMap
import io.github.dockyardmc.protocol.types.writeMap
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

data class ItemBlockStateComponent(val properties: Map<String, String>) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeMap(properties, ByteBuf::writeString, ByteBuf::writeString)
    }

    companion object : NetworkReadable<ItemBlockStateComponent> {
        override fun read(buffer: ByteBuf): ItemBlockStateComponent {
            return ItemBlockStateComponent(buffer.readMap(ByteBuf::readString, ByteBuf::readString))
        }
    }
}