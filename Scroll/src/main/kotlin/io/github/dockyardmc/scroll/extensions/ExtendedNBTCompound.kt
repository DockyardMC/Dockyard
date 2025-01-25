package io.github.dockyardmc.scroll.extensions

import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.serializers.NbtToComponentSerializer
import org.jglrxavpok.hephaistos.nbt.NBTCompound

fun NBTCompound.toComponent(): Component {
    return NbtToComponentSerializer.serializeNbt(this)
}