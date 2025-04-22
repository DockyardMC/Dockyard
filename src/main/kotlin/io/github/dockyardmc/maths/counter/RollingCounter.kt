package io.github.dockyardmc.maths.counter

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.maths.sin
import io.github.dockyardmc.scheduler.Scheduler
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.time.Duration

abstract class RollingCounter<T>(val scheduler: Scheduler) {
    abstract val value: Bindable<T>

    abstract var animatedDisplayValue: T

    var isRollingProportional: Boolean = false

    var rollingDuration: Duration = Duration.ZERO

    var rollingEasing: Easing = Easing.OUT_QUAD

    protected val animationProvider: AnimationProvider = AnimationProvider(scheduler)

    protected abstract fun getProportionalRollDuration(current: T, new: T): Duration

    protected abstract fun interpolate(start: T, end: T, progress: Float): T

    enum class Easing(val transform: (t: Float) -> Float) {
        IN_SINE({ value -> 1 - cos(value * PI.toFloat() / 2f) }),
        OUT_SINE({ value -> sin(value * PI.toFloat() / 2f) }),
        IN_QUAD({ value -> value * value }),
        OUT_QUAD({ value -> 1 - (1 - value) * (1 - value) }),
        IN_CUBIC({ value -> value * value * value }),
        OUT_CUBIC({ value -> 1 - (1 - value).pow(3f) }),
        IN_QUART({ value -> value * value * value * value }),
        OUT_QUART({ value -> 1 - (1 - value).pow(4) }),
        IN_EXPO({ value -> if (value == 0f) 0f else 2f.pow(10 * value - 10) }),
        OUT_EXPO({ value -> if (value == 1f) 1f else 1f - 2f.pow(-10f * value) }),
        IN_CIRC({ value -> sqrt(1f - value.pow(2f)) }),
        OUT_CIRC({ value -> sqrt(1f - (value - 1f).pow(2)) }),
        IN_ELASTIC({ value ->
            when (value) {
                0f -> 0f
                1f -> 1f
                else -> {
                    val p = 0.3f
                    val a = 1f
                    val s = p / 4f
                    -(a * 2f.pow(10 * (value - 1))) * sin((value - 1 - s) * (2 * PI.toFloat()) / p)
                }
            }
        }),
        OUT_ELASTIC({ value ->
            when (value) {
                0f -> 0f
                1f -> 1f
                else -> {
                    val p = 0.3f
                    val a = 1f
                    val s = p / 4f
                    a * 2f.pow(-10 * value) * sin((value - s) * (2 * PI.toFloat()) / p) + 1
                }
            }
        }),
        IN_BOUNCE({ value ->
            val n1 = 7.5625f
            val d1 = 2.75f
            val v = 1 - value
            if (v < 1 / d1) {
                n1 * v * v
            } else if (v < 2 / d1) {
                n1 * (v - 1.5f / d1) * (v - 1.5f / d1) + 0.75f
            } else if (v < 2.5 / d1) {
                n1 * (v - 2.25f / d1) * (v - 2.25f / d1) + 0.9375f
            } else {
                n1 * (v - 2.625f / d1) * (v - 2.625f / d1) + 0.984375f
            }
        }),
        OUT_BOUNCE({ value ->
            val n1 = 7.5625f
            val d1 = 2.75f
            if (value < 1 / d1) {
                n1 * value * value
            } else if (value < 2 / d1) {
                n1 * (value - 1.5f / d1) * (value - 1.5f / d1) + 0.75f
            } else if (value < 2.5 / d1) {
                n1 * (value - 2.25f / d1) * (value - 2.25f / d1) + 0.9375f
            } else {
                n1 * (value - 2.625f / d1) * (value - 2.625f / d1) + 0.984375f
            }
        });
    }
}