package io.github.dockyardmc.dialog.input

import io.github.dockyardmc.annotations.DialogDsl
import io.github.dockyardmc.nbt.nbt
import io.github.dockyardmc.protocol.NbtWritable
import io.github.dockyardmc.registry.registries.DialogInputType
import io.github.dockyardmc.scroll.extensions.toComponent
import net.kyori.adventure.nbt.CompoundBinaryTag

sealed class DialogInput : NbtWritable {
    abstract val key: String
    abstract val label: String
    abstract val type: DialogInputType

    override fun getNbt(): CompoundBinaryTag {
        return nbt {
            withString("key", key)
            withCompound("label", label.toComponent().toNBT())
            withString("type", type.getEntryIdentifier())
        }
    }

    @DialogDsl
    sealed class Builder(val key: String) {
        var label: String = ""

        abstract fun build() : DialogInput
    }
}