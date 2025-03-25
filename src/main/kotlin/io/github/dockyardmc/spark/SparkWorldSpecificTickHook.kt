package io.github.dockyardmc.spark

import io.github.dockyardmc.scheduler.runnables.ticks
import io.github.dockyardmc.scheduler.SchedulerTask
import io.github.dockyardmc.world.World
import me.lucko.spark.common.tick.AbstractTickHook

class SparkWorldSpecificTickHook(val world: World) : AbstractTickHook() {
    lateinit var task: SchedulerTask

    override fun start() {
        task = world.scheduler.runRepeating(1.ticks) {
            onTick()
        }
    }

    override fun close() {
        if (::task.isInitialized) task.cancel()
    }

}