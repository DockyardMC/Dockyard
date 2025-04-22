package io.github.dockyardmc.maths.counter

import cz.lukynka.bindables.Bindable
import cz.lukynka.bindables.BindableDispatcher
import io.github.dockyardmc.scheduler.Scheduler
import kotlin.math.abs
import kotlin.time.Duration

class RollingCounterDouble(scheduler: Scheduler) : RollingCounter<Double>(scheduler) {

    override val value: Bindable<Double> = Bindable<Double>(0.0)

    override var animatedDisplayValue: Double = value.value

    override fun getProportionalRollDuration(current: Double, new: Double): Duration {
        if (!isRollingProportional) return rollingDuration

        val difference = abs(new - current)
        return rollingDuration * difference
    }

    val rollDispatcher: BindableDispatcher<Double> = BindableDispatcher()

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

    override fun interpolate(start: Double, end: Double, progress: Float): Double {
        return (start + (end - start) * progress)
    }
}