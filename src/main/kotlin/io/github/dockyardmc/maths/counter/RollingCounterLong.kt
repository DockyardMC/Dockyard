package io.github.dockyardmc.maths.counter

import cz.lukynka.bindables.Bindable
import cz.lukynka.bindables.BindableDispatcher
import io.github.dockyardmc.scheduler.Scheduler
import kotlin.math.abs
import kotlin.math.roundToLong
import kotlin.time.Duration

class RollingCounterLong(scheduler: Scheduler) : RollingCounter<Long>(scheduler) {

    override val value: Bindable<Long> = Bindable<Long>(0)

    override var animatedDisplayValue: Long = value.value

    override fun getProportionalRollDuration(current: Long, new: Long): Duration {
        if (!isRollingProportional) return rollingDuration

        val difference = abs(new - current)
        return rollingDuration * difference.toInt()
    }

    val rollDispatcher: BindableDispatcher<Long> = BindableDispatcher()

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

    override fun interpolate(start: Long, end: Long, progress: Float): Long {
        return (start + (end - start) * progress).roundToLong()
    }
}