package io.github.dockyardmc.dialog.body

import io.github.dockyardmc.nbt.nbt
import io.github.dockyardmc.protocol.NbtWritable
import io.github.dockyardmc.registry.registries.DialogBodyType
import net.kyori.adventure.nbt.CompoundBinaryTag

sealed class DialogBody : NbtWritable {
    abstract val type: DialogBodyType

    override fun getNbt(): CompoundBinaryTag {
        return nbt {
            withString("type", type.getEntryIdentifier())
        }
    }
}