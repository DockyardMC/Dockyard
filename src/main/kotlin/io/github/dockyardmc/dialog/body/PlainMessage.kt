package io.github.dockyardmc.dialog.body

import io.github.dockyardmc.registry.DialogBodyTypes
import io.github.dockyardmc.scroll.extensions.put
import io.github.dockyardmc.scroll.extensions.toComponent
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class PlainMessage(
    val content: String,
    val width: Int = 200,
) : DialogBody {
    override fun getNbt(): NBTCompound {
        return NBT.Compound { builder ->
            builder.put("type", DialogBodyTypes.PLAIN_MESSAGE.getEntryIdentifier())

            builder.put("contents", content.toComponent().toNBT())
            builder.put("width", width)
        }
    }
}