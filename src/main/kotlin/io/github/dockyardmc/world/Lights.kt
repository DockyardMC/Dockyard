package io.github.dockyardmc.world

import java.util.*

data class Light(
    var skyMask: BitSet = BitSet(),
    var blockMask: BitSet = BitSet(),
    var emptySkyMask: BitSet = BitSet(),
    var emptyBlockMask: BitSet = BitSet(),
    var skyLight: ByteArray = ByteArray(0),
    var blockLight: ByteArray = ByteArray(0)
)
