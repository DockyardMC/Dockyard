package io.github.dockyardmc.dialog.form.submit

import io.github.dockyardmc.protocol.NbtWritable
import io.github.dockyardmc.registry.registries.DialogSubmitMethodType
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBT

abstract class DialogSubmitMethod : NbtWritable {
    abstract val type: DialogSubmitMethodType

    override fun getNbt(): NBT {
        return NBT.Compound { builder ->
            builder.put("type", type.getEntryIdentifier())
        }
    }
}