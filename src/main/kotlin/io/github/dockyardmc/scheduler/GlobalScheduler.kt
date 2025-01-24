package io.github.dockyardmc.scheduler

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.runnables.ticks
import java.util.concurrent.CompletableFuture
import kotlin.time.Duration


class GlobalScheduler(name: String) : Scheduler(name) {

    init {
        SchedulerManager.registerGlobal(this)
    }

    override fun dispose() {
        SchedulerManager.unregisterGlobal(this)
        super.dispose()
    }
}

@JvmName("runLaterAsyncTyped")
fun <T> runLaterAsync(duration: Duration, unit: () -> T): CompletableFuture<T> {
    return DockyardServer.scheduler.runLaterAsync(duration, unit)
}

@JvmName("runAsync")
fun <T> runAsync(unit: () -> T): CompletableFuture<T> {
    return DockyardServer.scheduler.runAsync(unit)
}


@JvmName("runLaterAsyncTypedTicks")
fun <T> runLaterAsync(ticks: Int, unit: () -> T): CompletableFuture<T> {
    return DockyardServer.scheduler.runLaterAsync(ticks.ticks, unit)
}

@JvmName("runLaterAsyncVoid")
fun runLaterAsync(duration: Duration, unit: () -> Unit): CompletableFuture<Unit> {
    return DockyardServer.scheduler.runLaterAsync<Unit>(duration, unit)
}

@JvmName("runLaterAsyncVoidTicks")
fun runLaterAsync(ticks: Int, unit: () -> Unit): CompletableFuture<Unit> {
    return DockyardServer.scheduler.runLaterAsync<Unit>(ticks.ticks, unit)
}
