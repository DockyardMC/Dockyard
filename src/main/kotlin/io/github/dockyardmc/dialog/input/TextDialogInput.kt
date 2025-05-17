package io.github.dockyardmc.dialog.input

import io.github.dockyardmc.registry.DialogInputTypes
import io.github.dockyardmc.registry.registries.DialogInputType
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class TextDialogInput(
    override val label: String,
    val width: Int = 200,
    val labelVisible: Boolean = true,
    val initial: String = "",
) : DialogInput() {
    override val type: DialogInputType = DialogInputTypes.TEXT

    override fun getNbt(): NBTCompound {
        return super.getNbt().kmodify {
            put("width", width)
            put("label_visible", labelVisible)
            put("initial", initial)
        }
    }
}