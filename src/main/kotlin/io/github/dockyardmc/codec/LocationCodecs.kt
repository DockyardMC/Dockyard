package io.github.dockyardmc.codec

import io.github.dockyardmc.maths.vectors.Vector3
import io.github.dockyardmc.tide.stream.StreamCodec

object LocationCodecs {

    val BLOCK_POSITION = StreamCodec.LONG.transform<Vector3>(
        { from ->
            val blockX = from.x.toLong()
            val blockY = from.y.toLong()
            val blockZ = from.z.toLong()
            ((blockX and 0x3FFFFFF shl 38) or ((blockZ and 0x3FFFFFF) shl 12) or (blockY and 0xFFF))
        },
        { to ->
            val x = (to shr 38).toInt()
            val y = (to shl 52 shr 52).toInt()
            val z = (to shl 26 shr 38).toInt()
            Vector3(x, y, z)
        })

}