package io.github.dockyardmc.maths

class Percentage(initialPercentage: Double) {
    var max: Double? = null
    var min: Double? = null

    private var innerPercentage: Double = initialPercentage
    var percentage get() = getInnerPercentage()
        set(value) = setInnerPercentage(value)

    @JvmName("getInnerPercentageMethod")
    private fun getInnerPercentage(): Double {
        var returnablePercentage = innerPercentage
        if(max != null) returnablePercentage = returnablePercentage.coerceAtMost(max!!)
        if(min != null) returnablePercentage = returnablePercentage.coerceAtLeast(min!!)
        return returnablePercentage
    }

    @JvmName("setInnerPercentageMethod")
    private fun setInnerPercentage(value: Double) {
        innerPercentage = value
    }

    fun getValueOf(value: Double): Double {
        return (percentage / 100) * value
    }

    fun getValueOf(value: Int): Int {
        return (percentage.toInt() / 100) * value
    }

    fun getValueOf(value: Float): Float {
        return (percentage.toFloat() / 100) * value
    }

    fun getValueOf(value: Long): Long {
        return (percentage.toLong() / 100) * value
    }

    fun setFromValue(value: Double, max: Double) {
        innerPercentage = percent(max, value)
    }

    fun setFromValue(value: Float, max: Float) {
        innerPercentage = percent(max, value).toDouble()
    }

    fun setFromValue(value: Int, max: Int) {
        innerPercentage = percent(max, value).toDouble()
    }

    fun setFromValue(value: Long, max: Long) {
        innerPercentage = percent(max, value).toDouble()
    }

    fun getNormalized(): Double {
        return getInnerPercentage() / 100.0
    }
}