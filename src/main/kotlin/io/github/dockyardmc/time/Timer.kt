package io.github.dockyardmc.time

import cz.lukynka.bindables.Bindable
import cz.lukynka.bindables.BindablePool
import cz.lukynka.prettylog.log
import io.github.dockyardmc.scheduler.CustomRateScheduler
import io.github.dockyardmc.utils.Disposable
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

class Timer(val durationUnit: DurationUnit = DurationUnit.SECONDS): Disposable {

    val bindablePool = BindablePool()
    private val scheduler = CustomRateScheduler(1.milliseconds, "timer-${UUID.randomUUID()}")

    val isPaused: Bindable<Boolean> = bindablePool.provideBindable(true)
    var tickRate = Bindable<Duration>(1.milliseconds)
    var currentTimeMs: Long = 0

    private var schedulerTask = scheduler.runRepeating(tickRate.value) {
        this.tick()
    }

    val timerTickDispatcher = bindablePool.provideBindableListener<Duration>()
    val timerPauseResumeDispatcher = bindablePool.provideBindableListener<Boolean>()

    init {
        tickRate.valueChanged { value ->
            scheduler.tickRate.value = value.newValue
        }

        isPaused.valueChanged { value ->
            scheduler.paused.value = value.newValue
            timerPauseResumeDispatcher.dispatch(value.newValue)
        }
        scheduler.resume()
        scheduler.tickRate.triggerUpdate()
    }

    fun pause() {
        isPaused.value = true
    }

    fun resume() {
        isPaused.value = false
    }

    fun tick() {
        currentTimeMs++
    }

    override fun dispose() {
        schedulerTask.cancel()
        scheduler.dispose()
        bindablePool.dispose()
    }
}