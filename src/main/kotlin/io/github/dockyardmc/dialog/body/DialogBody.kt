package io.github.dockyardmc.dialog.body

import io.github.dockyardmc.protocol.NbtWritable
import io.github.dockyardmc.registry.registries.DialogBodyType
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound

sealed class DialogBody : NbtWritable {
    abstract val type: DialogBodyType

    override fun getNbt(): NBTCompound {
        return NBT.Compound { builder ->
            builder.put("type", type.getEntryIdentifier())
        }
    }
}