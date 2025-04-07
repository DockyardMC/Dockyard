package io.github.dockyardmc.advancement

import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeStringArray
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.writeOptional
import io.netty.buffer.ByteBuf

data class Advancement(
    val id: String,
    val parentId: String?,
    val display: AdvancementDisplay?,
    val requirements: List<List<String>>,
) : NetworkWritable {
    override fun write(buffer: ByteBuf) {
        buffer.writeOptional(parentId, ByteBuf::writeString)
        buffer.writeOptional(display) { buf, it -> it.write(buf); buf }
        buffer.writeVarInt(requirements.size)
        requirements.forEach(buffer::writeStringArray)

        buffer.writeBoolean(false) // thats 'Sends telemerty' field
    }
}

