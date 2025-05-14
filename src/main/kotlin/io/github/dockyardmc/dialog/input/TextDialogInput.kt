package io.github.dockyardmc.dialog.input

import io.github.dockyardmc.registry.DialogInputTypes
import io.github.dockyardmc.registry.registries.DialogInputType
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class TextDialogInput(
    label: String,
    val width: Int = 200,
    val labelVisible: Boolean = true,
    val initial: String = ""
) : DialogInput(label) {
    override val type: DialogInputType = DialogInputTypes.TEXT

    override fun getNbt(): NBT {
        return (super.getNbt() as NBTCompound).kmodify {
            put("width", width)
            put("label_visible", labelVisible)
            put("initial", initial)
        }
    }
}