package io.github.dockyardmc.utils

import io.github.dockyardmc.scroll.CustomColor
import io.github.dockyardmc.scroll.LegacyTextColor

data class RangeColorMapper(val steps: Collection<Step>, val defaultColor: CustomColor = CustomColor.fromHex("#FFFFFF")) {

    constructor(defaultColor: CustomColor = CustomColor.fromHex("#FFFFFF"), vararg steps: Step) : this(steps.toList(), defaultColor)

    private val sortedSteps = steps.sortedByDescending { step -> step.aboveEquals }

    data class Step(val aboveEquals: Double, val color: CustomColor) {
        constructor(aboveEquals: Double, color: String) : this(aboveEquals, CustomColor.fromHex(color))
        constructor(aboveEquals: Double, color: LegacyTextColor) : this(aboveEquals, CustomColor.fromHex(color.hex))
    }

    operator fun get(string: String, value: Double): String {
        return "<${getColor(value).toHex()}>$string"
    }

    fun getColor(value: Double): CustomColor {
        sortedSteps.forEach { step ->
            if (value >= step.aboveEquals) return step.color
        }

        return defaultColor
    }
}