package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

data class SeededContainerLootComponent(val lootTable: String, val seed: Long) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeString(lootTable)
        buffer.writeLong(seed)
    }

    companion object : NetworkReadable<SeededContainerLootComponent> {
        override fun read(buffer: ByteBuf): SeededContainerLootComponent {
            return SeededContainerLootComponent(buffer.readString(), buffer.readLong())
        }
    }
}