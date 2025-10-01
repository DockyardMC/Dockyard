package io.github.dockyardmc.codec

import io.github.dockyardmc.tide.codec.Codec
import io.github.dockyardmc.tide.stream.StreamCodec

object ExtraCodecs {

    object BitSet {
        val STREAM = StreamCodec.LONG_ARRAY.transform<java.util.BitSet>(java.util.BitSet::toLongArray, java.util.BitSet::valueOf)
        val CODEC = Codec.LONG_ARRAY.transform<java.util.BitSet>(java.util.BitSet::valueOf, java.util.BitSet::toLongArray)
    }

}