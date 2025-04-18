package io.github.dockyardmc.scheduler

import io.github.dockyardmc.scheduler.SchedulerTask.Status
import io.github.dockyardmc.scheduler.SchedulerTask.Type
import java.util.concurrent.CompletableFuture

class AsyncSchedulerTask<T>(val task: ((AsyncSchedulerTask<T>) -> T), val type: Type, val name: String? = null) {

    private var innerStatus = Status.WAITING
    val status get() = innerStatus
    var cancelled: Boolean = false

    val future = CompletableFuture<T>()

    fun run(tick: Long) {
        innerStatus = Status.RUNNING
        try {
            val t = task.invoke(this)
            future.complete(t)
            innerStatus = Status.FINISHED
        } catch (ex: Exception) {
            innerStatus = Status.THROW
            future.completeExceptionally(ex)
        }
    }

    fun cancel() {
        cancelled = true
    }

    override fun toString(): String {
        return "AsyncSchedulerTask(type=${type.name}, status=${status.name}, cancelled=${cancelled})"
    }

}