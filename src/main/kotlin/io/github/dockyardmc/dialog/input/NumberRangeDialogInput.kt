package io.github.dockyardmc.dialog.input

import io.github.dockyardmc.annotations.DialogDsl
import io.github.dockyardmc.extentions.modify
import io.github.dockyardmc.registry.DialogInputTypes
import io.github.dockyardmc.registry.registries.DialogInputType
import net.kyori.adventure.nbt.CompoundBinaryTag

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

    override fun getNbt(): CompoundBinaryTag {
        return super.getNbt().modify {
            withString("label_format", labelFormat)
            withInt("width", width)
            withDouble("start", range.start)
            withDouble("end", range.endInclusive)
            initial?.apply { withDouble("initial", initial) }
            step?.apply { withFloat("step", step) }
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