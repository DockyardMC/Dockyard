package io.github.dockyardmc.dialog.input

import io.github.dockyardmc.protocol.NbtWritable
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class DialogFormInput(
    val key: String,
    val input: DialogInput,
) : NbtWritable {
    override fun getNbt(): NBTCompound {
        return NBT.Compound { builder ->
            builder.put("key", key)
            builder.putAll(input.getNbt())
        }
    }
}