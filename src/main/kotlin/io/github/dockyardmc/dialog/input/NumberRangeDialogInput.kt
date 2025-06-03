package io.github.dockyardmc.dialog.input

import io.github.dockyardmc.registry.DialogInputTypes
import io.github.dockyardmc.registry.registries.DialogInputType
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class NumberRangeDialogInput(
    override val key: String,
    override val label: String,
    val min: Double,
    val max: Double,
    val step: Float?,
    val width: Int = 200,
    val initial: Double? = null,
    val labelFormat: String = "options.generic_value",
) : DialogInput() {
    override val type: DialogInputType = DialogInputTypes.NUMBER_RANGE

    init {
        step?.let {
            if (it < 0) throw IllegalArgumentException("step must be positive")
        }
    }

    override fun getNbt(): NBTCompound {
        return super.getNbt().kmodify {
            put("label_format", labelFormat)
            put("width", width)
            put("start", min)
            put("end", max)
            put("initial", initial)
            put("step", step)
        }
    }
}