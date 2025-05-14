package io.github.dockyardmc.dialog.input

import io.github.dockyardmc.protocol.NbtWritable
import io.github.dockyardmc.registry.registries.DialogInputType
import io.github.dockyardmc.scroll.extensions.put
import io.github.dockyardmc.scroll.extensions.toComponent
import org.jglrxavpok.hephaistos.nbt.NBT

abstract class DialogInput(
    val label: String,
) : NbtWritable {
    abstract val type: DialogInputType

    override fun getNbt(): NBT {
        return NBT.Compound { builder ->
            builder.put("label", label.toComponent().toNBT())
            builder.put("type", type.getEntryIdentifier())
        }
    }
}