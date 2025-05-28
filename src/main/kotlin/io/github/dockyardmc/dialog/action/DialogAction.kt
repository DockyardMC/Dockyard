package io.github.dockyardmc.dialog.action

import io.github.dockyardmc.protocol.NbtWritable
import io.github.dockyardmc.registry.registries.DialogActionType
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound

sealed class DialogAction : NbtWritable {
    abstract val type: DialogActionType

    override fun getNbt(): NBTCompound {
        return NBT.Compound { builder ->
            builder.put("type", type.getEntryIdentifier())
        }
    }
}