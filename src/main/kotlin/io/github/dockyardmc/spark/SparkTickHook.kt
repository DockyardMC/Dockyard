package io.github.dockyardmc.spark

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.runnables.ticks
import io.github.dockyardmc.scheduler.SchedulerTask
import me.lucko.spark.common.tick.AbstractTickHook

class SparkTickHook: AbstractTickHook() {
    lateinit var task: SchedulerTask

    override fun start() {
        task = DockyardServer.scheduler.runRepeating(1.ticks) {}
    }

    override fun close() {
        if(::task.isInitialized) task.cancel()
    }

}