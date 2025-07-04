package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.extentions.readEnum
import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

data class TropicalFishPatternComponent(val pattern: Pattern) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeEnum(pattern)
    }

    override fun hashStruct(): HashHolder {
        return StaticHash(CRC32CHasher.ofEnum(pattern))
    }

    companion object : NetworkReadable<TropicalFishPatternComponent> {
        override fun read(buffer: ByteBuf): TropicalFishPatternComponent {
            return TropicalFishPatternComponent(buffer.readEnum())
        }
    }

    enum class Pattern {
        KOB,
        SUNSTREAK,
        SNOOPER,
        DASHER,
        BRINELY,
        SPOTTY,
        FLOPPER,
        STRIPEY,
        GLITTER,
        BLOCKFISH,
        BETY,
        CLAYFISH
    }
}