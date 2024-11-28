package io.github.dockyardmc.scheduler

import cz.lukynka.Bindable
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.runnables.RepeatingTimer
import io.github.dockyardmc.runnables.ticks
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


class CustomRateScheduler(initialTickRate: Duration = 50.milliseconds) : Scheduler() {

    var tickRate = Bindable<Duration>(initialTickRate)
    var paused = Bindable<Boolean>(false)
    private var tickRateMs: Long = 50

    fun syncWith(scheduler: Scheduler) {
        scheduler.runLater(1.ticks) {
            when(scheduler) {
                is GlobalScheduler -> tickRate.value = 50.milliseconds
                is CustomRateScheduler -> tickRate.value = scheduler.tickRate.value
            }
        }
    }

    fun syncWithGlobalScheduler() {
        syncWith(DockyardServer.scheduler)
    }

    lateinit var tickRateTimer: RepeatingTimer

    fun setTickRate(tickRate: Duration) {
        this.tickRate.value = tickRate
    }

    fun setTickRate(milliseconds: Long) {
        this.tickRate.value = milliseconds.milliseconds
    }

    fun pause() {
        paused.value = true
    }

    fun resume() {
        paused.value = false
    }

    init {
        tickRate.valueChanged {
            tickRateMs = it.newValue.inWholeMilliseconds

            if(::tickRateTimer.isInitialized) tickRateTimer.dispose()
            tickRateTimer = RepeatingTimer(tickRateMs) {
                executorService.submit {
                    tick()
                }
            }
            tickRateTimer.start()
        }
        tickRate.triggerUpdate()

        paused.valueChanged { tickRateTimer.paused = it.newValue }

        tickRate.triggerUpdate()
    }

    override fun dispose() {
        tickRate.dispose()
        tickRateTimer.dispose()
        super.dispose()
    }
}