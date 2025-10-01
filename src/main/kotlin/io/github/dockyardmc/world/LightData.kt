package io.github.dockyardmc.world

import io.github.dockyardmc.codec.ExtraCodecs
import io.github.dockyardmc.tide.codec.Codec
import io.github.dockyardmc.tide.codec.StructCodec
import io.github.dockyardmc.tide.stream.StreamCodec
import io.netty.buffer.ByteBuf
import java.util.*

data class LightData(
    val skyMask: BitSet = BitSet(),
    val blockMask: BitSet = BitSet(),
    val emptySkyMask: BitSet = BitSet(),
    val emptyBlockMask: BitSet = BitSet(),
    val skyLight: List<ByteBuf> = mutableListOf(),
    val blockLight: List<ByteBuf> = mutableListOf()
) {
    companion object {

        val STREAM_CODEC = StreamCodec.of(
            ExtraCodecs.BitSet.STREAM, LightData::skyMask,
            ExtraCodecs.BitSet.STREAM, LightData::blockMask,
            ExtraCodecs.BitSet.STREAM, LightData::emptySkyMask,
            ExtraCodecs.BitSet.STREAM, LightData::emptyBlockMask,
            StreamCodec.BYTE_ARRAY.list(), LightData::skyLight,
            StreamCodec.BYTE_ARRAY.list(), LightData::blockLight,
            ::LightData
        )

        val CODEC = StructCodec.of(
            "sky_mask", ExtraCodecs.BitSet.CODEC, LightData::skyMask,
            "block_mask", ExtraCodecs.BitSet.CODEC, LightData::blockMask,
            "empty_sky_mask", ExtraCodecs.BitSet.CODEC, LightData::emptySkyMask,
            "empty_block_mask", ExtraCodecs.BitSet.CODEC, LightData::emptyBlockMask,
            "sky_light", Codec.BYTE_BUFFER.list(), LightData::skyLight,
            "block_light", Codec.BYTE_BUFFER.list(), LightData::blockLight,
            ::LightData
        )
    }
}
