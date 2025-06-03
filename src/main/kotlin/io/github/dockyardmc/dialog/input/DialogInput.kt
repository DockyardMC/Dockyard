package io.github.dockyardmc.dialog.input

import io.github.dockyardmc.annotations.DialogDsl
import io.github.dockyardmc.protocol.NbtWritable
import io.github.dockyardmc.registry.registries.DialogInputType
import io.github.dockyardmc.scroll.extensions.put
import io.github.dockyardmc.scroll.extensions.toComponent
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound

sealed class DialogInput : NbtWritable {
    abstract val key: String
    abstract val label: String
    abstract val type: DialogInputType

    override fun getNbt(): NBTCompound {
        return NBT.Compound { builder ->
            builder.put("key", key)
            builder.put("label", label.toComponent().toNBT())
            builder.put("type", type.getEntryIdentifier())
        }
    }

    @DialogDsl
    sealed class Builder(val key: String) {
        var label: String = ""

        abstract fun build() : DialogInput
    }
}