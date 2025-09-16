package io.github.dockyardmc.codec

import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import net.kyori.adventure.nbt.CompoundBinaryTag

object ComponentCodecs {
    val STREAM = BinaryTagCodecs.STREAM.transform({ from -> from.toNBT() }, { to -> (to as CompoundBinaryTag).toComponent() })
    val STRING = BinaryTagCodecs.STRING.transform<Component>({ from -> (from as CompoundBinaryTag).toComponent() }, { to -> to.toNBT() })
}

