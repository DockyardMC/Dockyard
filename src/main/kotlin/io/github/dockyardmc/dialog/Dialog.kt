package io.github.dockyardmc.dialog

import io.github.dockyardmc.dialog.body.DialogBody
import io.github.dockyardmc.protocol.NbtWritable
import io.github.dockyardmc.registry.registries.DialogType
import io.github.dockyardmc.scroll.extensions.put
import io.github.dockyardmc.scroll.extensions.toComponent
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTList
import org.jglrxavpok.hephaistos.nbt.NBTType

abstract class Dialog(
    val title: String,
    val externalTitle: String?, // TODO: figure out what the fuck is that
    val canCloseWithEsc: Boolean,
    val body: List<DialogBody>,
) : NbtWritable {
    abstract val type: DialogType

    override fun getNbt(): NBT {
        return NBT.Compound { builder ->
            builder.put("type", type.getEntryIdentifier())
            builder.put("title", title.toComponent().toNBT())

            if (externalTitle != null)
                builder.put("external_title", externalTitle.toComponent().toNBT())

            builder.put("can_close_with_escape", canCloseWithEsc)
            builder.put("body", NBTList(NBTType.TAG_Compound, body.map { it.getNbt() }))

        }
    }
}