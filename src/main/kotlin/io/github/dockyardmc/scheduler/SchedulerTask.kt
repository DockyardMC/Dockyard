package io.github.dockyardmc.scheduler

import cz.lukynka.prettylog.log
import java.lang.Exception

class SchedulerTask(val task: ((serverTicks: Long, schedulerTicks: Long) -> Unit), val type: Type, val name: String? = null) {

    private var innerStatus = Status.WAITING
    val status get() = innerStatus
    var cancelled: Boolean = false

    fun run(serverTicks: Long, schedulerTicks: Long) {
        innerStatus = Status.RUNNING
        try {
            task.invoke(serverTicks, schedulerTicks)
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

    enum class Status {
        WAITING,
        RUNNING,
        FINISHED,
        THROW
    }

    enum class Type {
        IMMEDIATE,
        TICK_START,
        TICK_END,
        REPEATING_TICK_START,
        REPEATING_TICK_END,
    }
}
