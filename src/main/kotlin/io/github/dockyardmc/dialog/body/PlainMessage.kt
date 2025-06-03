package io.github.dockyardmc.dialog.body

import io.github.dockyardmc.annotations.DialogDsl
import io.github.dockyardmc.registry.DialogBodyTypes
import io.github.dockyardmc.registry.registries.DialogBodyType
import io.github.dockyardmc.scroll.extensions.put
import io.github.dockyardmc.scroll.extensions.toComponent
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class PlainMessage(
    val content: String,
    val width: Int = 200,
) : DialogBody() {
    override val type: DialogBodyType = DialogBodyTypes.PLAIN_MESSAGE

    init {
        if (width < 1 || width > 1024) throw IllegalArgumentException("width must be between 1 and 1024 (inclusive)")
    }

    override fun getNbt(): NBTCompound {
        return NBT.Compound { builder ->
            builder.put("contents", content.toComponent().toNBT())
            builder.put("width", width)
        }
    }

    @DialogDsl
    class Builder(val content: String) {
        var width: Int = 200

        fun build(): PlainMessage {
            return PlainMessage(content, width)
        }
    }
}