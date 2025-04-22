package io.github.dockyardmc.maths.counter

import cz.lukynka.bindables.Bindable
import cz.lukynka.bindables.BindableDispatcher
import io.github.dockyardmc.scheduler.Scheduler
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.time.Duration

class RollingCounterInt(scheduler: Scheduler) : RollingCounter<Int>(scheduler) {

    override val value: Bindable<Int> = Bindable<Int>(0)

    override var animatedDisplayValue: Int = value.value

    override fun getProportionalRollDuration(current: Int, new: Int): Duration {
        if (!isRollingProportional) return rollingDuration

        val difference = abs(new - current)
        return rollingDuration * difference
    }

    val rollDispatcher: BindableDispatcher<Int> = BindableDispatcher()

    init {
        value.valueChanged { event ->
            if(event.newValue == event.oldValue) return@valueChanged
            animationProvider.stop()

            animationProvider.start(getProportionalRollDuration(event.oldValue, event.newValue), rollingEasing) { progress ->
                val new = interpolate(event.oldValue, event.newValue, progress)
                if (animatedDisplayValue != new) {
                    animatedDisplayValue = new
                    rollDispatcher.dispatch(animatedDisplayValue)
                }
            }
        }
    }

    override fun interpolate(start: Int, end: Int, progress: Float): Int {
        return (start + (end - start) * progress).roundToInt()
    }
}