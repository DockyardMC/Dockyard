package io.github.dockyardmc.dialog.body

import io.github.dockyardmc.annotations.DialogDsl
import io.github.dockyardmc.extentions.modify
import io.github.dockyardmc.registry.DialogBodyTypes
import io.github.dockyardmc.registry.registries.DialogBodyType
import io.github.dockyardmc.scroll.extensions.toComponent
import net.kyori.adventure.nbt.CompoundBinaryTag

class PlainMessage(
    val content: String,
    val width: Int = 200,
) : DialogBody() {
    override val type: DialogBodyType = DialogBodyTypes.PLAIN_MESSAGE

    init {
        if (width < 1 || width > 1024) throw IllegalArgumentException("width must be between 1 and 1024 (inclusive)")
    }

    override fun getNbt(): CompoundBinaryTag {
        return super.getNbt().modify {
            withCompound("contents", content.toComponent().toNBT())
            withInt("width", width)
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