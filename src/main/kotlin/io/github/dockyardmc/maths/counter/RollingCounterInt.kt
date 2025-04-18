package io.github.dockyardmc.maths.counter

import cz.lukynka.bindables.Bindable
import kotlin.math.abs
import kotlin.time.Duration

class RollingCounterInt: RollingCounter<Int>() {

    override val innerValue: Bindable<Int> = Bindable<Int>(0)

    override fun getProportionalRollDuration(current: Int, new: Int): Duration {
        val difference = abs(new - current)
        return rollingDuration * difference
    }

    override fun set(value: Int) {
        
    }

    override fun remove(value: Int) {
    }

    override fun add(value: Int) {
    }

}