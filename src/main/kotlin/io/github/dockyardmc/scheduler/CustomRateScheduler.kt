package io.github.dockyardmc.scheduler

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.extentions.round
import io.github.dockyardmc.scheduler.runnables.RepeatingTimer
import io.github.dockyardmc.scheduler.runnables.ticks
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


class CustomRateScheduler(name: String, initialTickRate: Duration = 50.milliseconds) : Scheduler(name) {

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

    override fun updateMSPT() {
        val diff = Instant.now().toEpochMilli() - timeSinceLastTick.toEpochMilli()
        averages.add(diff)
        timeSinceLastTick = Instant.now()
        mspt = averages.average().round(1)
        if (mspt < tickRateMs) mspt = tickRateMs.toDouble()
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
            averages.clear()
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