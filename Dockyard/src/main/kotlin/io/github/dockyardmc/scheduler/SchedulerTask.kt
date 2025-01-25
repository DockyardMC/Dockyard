package io.github.dockyardmc.scheduler

import cz.lukynka.prettylog.log
import java.lang.Exception

class SchedulerTask(val task: (() -> Unit), val type: Type, val name: String? = null) {

    private var innerStatus = Status.WAITING
    val status get() = innerStatus
    var cancelled: Boolean = false

    fun run(tick: Long) {
        innerStatus = Status.RUNNING
        try {
            task.invoke()
            innerStatus = Status.FINISHED
        } catch (ex: Exception) {
            innerStatus = Status.THROW
            val message = buildString {
                append("Exception was thrown in scheduled task")
                if(name != null) append(" $name")
                append(":")
            }
            log(message)
            log(ex)
        }
    }

    fun cancel() {
        cancelled = true
    }

    enum class Status {
        WAITING,
        RUNNING,
        FINISHED,
        THROW
    }

    enum class Type {
        IMMEDIATE,
        TICK,
        REPEATING,
    }

    override fun toString(): String {
        return "SchedulerTask(type=${type.name}, status=${status.name}, cancelled=${cancelled})"
    }
}
