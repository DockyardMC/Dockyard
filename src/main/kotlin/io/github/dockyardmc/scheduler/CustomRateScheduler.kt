package io.github.dockyardmc.scheduler

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.extentions.round
import io.github.dockyardmc.runnables.RepeatingTimer
import io.github.dockyardmc.runnables.ticks
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


class CustomRateScheduler(initialTickRate: Duration = 50.milliseconds, name: String) : Scheduler(name) {

    constructor(name: String): this(50.milliseconds, name)

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

    override fun addTask(task: SchedulerTask, time: Duration) {
        val timeInTicks = (time.inWholeMilliseconds / tickRate.value.inWholeMilliseconds)
        val ticks = if (task.type == SchedulerTask.Type.TICK) timeInTicks + ticks else timeInTicks
        when (task.type) {
            SchedulerTask.Type.IMMEDIATE -> handleTask(task)
            SchedulerTask.Type.TICK -> {
                var list = scheduledTasks[ticks]
                if (list == null) {
                    scheduledTasks[ticks] = mutableListOf()
                    list = scheduledTasks[ticks]!!
                }

                list.add(task)
            }

            SchedulerTask.Type.REPEATING -> {
                var list = repeatingTasks[ticks]
                if (list == null) {
                    repeatingTasks[ticks] = mutableListOf()
                    list = repeatingTasks[ticks]!!
                }
                list.add(task)
            }
        }
    }

    override fun dispose() {
        tickRate.dispose()
        tickRateTimer.dispose()
        super.dispose()
    }
}