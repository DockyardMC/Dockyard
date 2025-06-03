package io.github.dockyardmc.dialog.input

import io.github.dockyardmc.annotations.DialogDsl
import io.github.dockyardmc.registry.DialogInputTypes
import io.github.dockyardmc.registry.registries.DialogInputType
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class NumberRangeDialogInput(
    override val key: String,
    override val label: String,
    val range: ClosedFloatingPointRange<Double>,
    val step: Float?,
    val width: Int,
    val initial: Double?,
    val labelFormat: String,
) : DialogInput() {
    override val type: DialogInputType = DialogInputTypes.NUMBER_RANGE

    init {
        step?.let {
            if (it < 0) throw IllegalArgumentException("step must be positive")
        }
        if (width < 1 || width > 1024) throw IllegalArgumentException("width must be between 1 and 1024 (inclusive)")
    }

    override fun getNbt(): NBTCompound {
        return super.getNbt().kmodify {
            put("label_format", labelFormat)
            put("width", width)
            put("start", range.start)
            put("end", range.endInclusive)
            put("initial", initial)
            put("step", step)
        }
    }

    @DialogDsl
    class Builder(key: String, val range: ClosedFloatingPointRange<Double>) : DialogInput.Builder(key) {
        var step: Float? = null
        var width: Int = 200
        var initial: Double? = null
        var labelFormat: String = "options.generic_value"

        override fun build(): NumberRangeDialogInput {
            return NumberRangeDialogInput(key, label, range, step, width, initial, labelFormat)
        }
    }
}