package io.github.dockyardmc.dialog.input

import io.github.dockyardmc.registry.DialogInputTypes
import io.github.dockyardmc.registry.registries.DialogInputType
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class NumberRangeDialogInput(
    label: String,
    val min: Double,
    val max: Double,
    val steps: Int,
    val width: Int = 200,
    val initial: Double? = null,
    val labelFormat: String = "options.generic_value"
) : DialogInput(label) {
    override val type: DialogInputType
        get() = DialogInputTypes.NUMBER_RANGE

    override fun getNbt(): NBT {
        return (super.getNbt() as NBTCompound).kmodify {
            put("label_format", labelFormat)
            put("width", width)
            put("start", min)
            put("end", max)
            put("initial", initial)
            put("steps", steps)
        }
    }
}