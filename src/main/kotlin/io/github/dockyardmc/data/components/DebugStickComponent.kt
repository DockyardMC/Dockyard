package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.readMap
import io.github.dockyardmc.protocol.types.writeMap
import io.netty.buffer.ByteBuf

class DebugStickComponent(val state: Map<String, String>) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeMap(state, ByteBuf::writeString, ByteBuf::writeString)
    }

    override fun hashStruct(): HashHolder {
        val hashedMap: Map<Int, Int> = state.mapValues { entry -> CRC32CHasher.ofString(entry.value) }.mapKeys { entry -> CRC32CHasher.ofString(entry.key) }
        return StaticHash(CRC32CHasher.ofMap(hashedMap))
    }

    companion object : NetworkReadable<DebugStickComponent> {
        override fun read(buffer: ByteBuf): DebugStickComponent {
            return DebugStickComponent(buffer.readMap(ByteBuf::readString, ByteBuf::readString))
        }
    }
}


