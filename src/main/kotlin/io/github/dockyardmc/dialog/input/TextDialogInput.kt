package io.github.dockyardmc.dialog.input

import io.github.dockyardmc.protocol.NbtWritable
import io.github.dockyardmc.registry.DialogInputTypes
import io.github.dockyardmc.registry.registries.DialogInputType
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class TextDialogInput(
    override val key: String,
    override val label: String,
    val width: Int = 200,
    val labelVisible: Boolean = true,
    val initial: String = "",
    val maxLength: Int = 32,
    val multiline: Multiline?,
) : DialogInput() {
    override val type: DialogInputType = DialogInputTypes.TEXT

    init {
        if (maxLength < 0) throw IllegalArgumentException("maxLength must be positive")
    }

    override fun getNbt(): NBTCompound {
        return super.getNbt().kmodify {
            put("width", width)
            put("label_visible", labelVisible)
            put("initial", initial)
            put("max_length", maxLength)
            multiline?.let { put("multiline", it.getNbt()) }
        }
    }

    class Multiline(
        val maxLines: Int?,
        val height: Int?,
    ) : NbtWritable {
        init {
            maxLines?.let {
                if (it < 0) throw IllegalArgumentException("maxLines must be positive")
            }
            height?.let {
                if (it < 0) throw IllegalArgumentException("height must be positive")
            }
        }

        override fun getNbt(): NBTCompound {
            return NBT.Compound {
                it.put("max_lines", maxLines)
                it.put("height", height)
            }
        }
    }
}