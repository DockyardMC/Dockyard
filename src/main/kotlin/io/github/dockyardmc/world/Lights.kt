package io.github.dockyardmc.world

import io.github.dockyardmc.extentions.readByteArray
import io.github.dockyardmc.extentions.readList
import io.github.dockyardmc.extentions.writeByteArray
import io.github.dockyardmc.extentions.writeLongArray
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.writeArray
import io.netty.buffer.ByteBuf
import java.util.*

@Suppress("ArrayInDataClass")
data class ChunkLight(
    var skyMask: BitSet = BitSet(),
    var blockMask: BitSet = BitSet(),
    var emptySkyMask: BitSet = BitSet(),
    var emptyBlockMask: BitSet = BitSet(),
    var skyLight: Array<ByteArray> = arrayOf(),
    var blockLight: Array<ByteArray> = arrayOf()
): NetworkWritable {

    fun reset() {
        skyMask = BitSet()
        blockMask = BitSet()
        emptySkyMask = BitSet()
        emptyBlockMask = BitSet()
//        skyLight = arrayOf()
//        blockLight = arrayOf()
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeLongArray(skyMask.toLongArray())
        buffer.writeLongArray(blockMask.toLongArray())
        buffer.writeLongArray(emptySkyMask.toLongArray())
        buffer.writeLongArray(emptyBlockMask.toLongArray())
        buffer.writeArray(skyLight, ByteBuf::writeByteArray)
        buffer.writeArray(blockLight, ByteBuf::writeByteArray)
    }

    companion object {
        fun read(buffer: ByteBuf): ChunkLight {
            val skyMask = BitSet.valueOf(buffer.readList(ByteBuf::readLong).toLongArray())
            val blockMask = BitSet.valueOf(buffer.readList(ByteBuf::readLong).toLongArray())
            val emptySkyMask = BitSet.valueOf(buffer.readList(ByteBuf::readLong).toLongArray())
            val emptyBlockMask = BitSet.valueOf(buffer.readList(ByteBuf::readLong).toLongArray())

            val skyLight = buffer.readList(ByteBuf::readByteArray).toTypedArray()
            val blockLight = buffer.readList(ByteBuf::readByteArray).toTypedArray()

            return ChunkLight(skyMask, blockMask, emptySkyMask, emptyBlockMask, skyLight, blockLight)
        }
    }
}
