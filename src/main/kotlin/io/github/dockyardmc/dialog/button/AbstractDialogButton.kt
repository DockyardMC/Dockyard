package io.github.dockyardmc.dialog.button

import io.github.dockyardmc.protocol.NbtWritable
import io.github.dockyardmc.scroll.extensions.put
import io.github.dockyardmc.scroll.extensions.toComponent
import org.jglrxavpok.hephaistos.nbt.NBT

abstract class AbstractDialogButton(
    val label: String,
    val tooltip: String?,
    val width: Int
) : NbtWritable {
    override fun getNbt(): NBT {
        return NBT.Compound { builder ->
            builder.put("label", label.toComponent().toNBT())
            if (tooltip != null)
                builder.put("tooltip", tooltip.toComponent().toNBT())
            builder.put("width", width)
        }
    }
}