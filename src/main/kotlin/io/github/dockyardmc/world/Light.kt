package io.github.dockyardmc.world

import io.github.dockyardmc.extentions.writeByteArray
import io.github.dockyardmc.extentions.writeLongArray
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.types.writeList
import io.netty.buffer.ByteBuf
import java.util.*

class Light(
    val lightEngine: LightEngine
) : NetworkWritable {
    val skyMask: BitSet = BitSet()
    val blockMask: BitSet = BitSet()
    val emptySkyMask: BitSet = BitSet()
    val emptyBlockMask: BitSet = BitSet()
    val skyLight: MutableList<ByteArray> = mutableListOf()
    val blockLight: MutableList<ByteArray> = mutableListOf()

    init {
        // first section is below the world. how awesome. we love minecraft
        emptySkyMask.set(0)
        emptyBlockMask.set(0)

        // last section is one section above the world. why.
        emptySkyMask.set(skyLight.size + 1)
        emptyBlockMask.set(skyLight.size + 1)

        lightEngine.skyLight.indices.forEach { i ->
            if (lightEngine.hasNonZeroData(lightEngine.skyLight[i])) {
                skyMask.set(i + 1)
                skyLight.add(lightEngine.skyLight[i])
            } else {
                emptySkyMask.set(i + 1)
            }

            if (lightEngine.hasNonZeroData(lightEngine.blockLight[i])) {
                blockMask.set(i + 1)
                blockLight.add(lightEngine.blockLight[i])
            } else {
                emptyBlockMask.set(i + 1)
            }
        }
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeLongArray(skyMask.toLongArray().toList())
        buffer.writeLongArray(blockMask.toLongArray().toList())
        buffer.writeLongArray(emptySkyMask.toLongArray().toList())
        buffer.writeLongArray(emptyBlockMask.toLongArray().toList())
        buffer.writeList(skyLight, ByteBuf::writeByteArray)
        buffer.writeList(blockLight, ByteBuf::writeByteArray)
    }
}
