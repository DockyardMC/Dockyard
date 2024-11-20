package io.github.dockyardmc.scheduler

import io.github.dockyardmc.scheduler.SchedulerTask.Status
import io.github.dockyardmc.scheduler.SchedulerTask.Type
import java.util.concurrent.CompletableFuture

class AsyncSchedulerTask<T>(val task: (() -> T), val type: Type, val name: String? = null) {

    private var innerStatus = Status.WAITING
    val status get() = innerStatus
    var cancelled: Boolean = false

    val future = CompletableFuture<T>()

    fun run() {
        innerStatus = Status.RUNNING
        try {
            innerStatus = Status.FINISHED
            val t = task.invoke()
            future.complete(t)
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