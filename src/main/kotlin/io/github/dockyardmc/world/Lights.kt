package io.github.dockyardmc.world

import java.util.*

data class Light(
    var skyMask: BitSet = BitSet(),
    var blockMask: BitSet = BitSet(),
    var emptySkyMask: BitSet = BitSet(),
    var emptyBlockMask: BitSet = BitSet(),
    var skyLight: List<ByteArray> = emptyList(),
    var blockLight: List<ByteArray> = emptyList()
)
