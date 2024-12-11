package io.github.dockyardmc.world.chunk

import java.util.*


data class ChunkLightData(
    var skyMask: BitSet = BitSet(),
    var blockMask: BitSet = BitSet(),
    var emptySkyMask: BitSet = BitSet(),
    var emptyBlockMask: BitSet = BitSet(),
    var skyLight: List<ByteArray> = listOf(),
    var blockLight: List<ByteArray> = listOf(),
)
