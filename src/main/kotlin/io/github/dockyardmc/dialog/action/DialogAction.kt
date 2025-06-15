package io.github.dockyardmc.dialog.action

import io.github.dockyardmc.nbt.nbt
import io.github.dockyardmc.protocol.NbtWritable
import io.github.dockyardmc.registry.registries.DialogActionType
import net.kyori.adventure.nbt.CompoundBinaryTag

sealed class DialogAction : NbtWritable {
    abstract val type: DialogActionType

    override fun getNbt(): CompoundBinaryTag {
        return nbt {
            withString("type", type.getEntryIdentifier())
        }
    }
}