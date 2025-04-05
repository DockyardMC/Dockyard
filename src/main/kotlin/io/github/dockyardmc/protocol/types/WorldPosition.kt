package io.github.dockyardmc.protocol.types

import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.location.readBlockPosition
import io.github.dockyardmc.location.writeBlockPosition
import io.github.dockyardmc.maths.vectors.Vector3
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.netty.buffer.ByteBuf

data class WorldPosition(val dimension: String, val blockPosition: Vector3) : NetworkWritable {

    override fun write(buffer: ByteBuf) {
        buffer.writeString(dimension)
        buffer.writeBlockPosition(blockPosition)
    }

    companion object : NetworkReadable<WorldPosition> {
        override fun read(buffer: ByteBuf): WorldPosition {
            val name = buffer.readString()
            val blockPos = buffer.readBlockPosition()

            return WorldPosition(name, blockPos)
        }
    }

}