package io.github.dockyardmc.periodic

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerTickEvent


object Period {
    val periodMap = mutableMapOf<Class<out Periodic>, MutableList<ExecutablePeriodic<Periodic>>>()

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Periodic> on(noinline function: (event: T) -> Unit) {
        val eventType = T::class.java
        val eventList = periodMap.getOrPut(eventType) { mutableListOf() }
        eventList.add(ExecutablePeriodic(function as (Periodic) -> Unit))
    }

    fun dispatch(event: Periodic) {
        val eventType = event.javaClass
        periodMap[eventType]?.forEach { executablePeriodic ->
            executablePeriodic.function.invoke(event)
        }
    }

    init {
        var currentTicks = 0
        var currentSeconds = 0
        var currentMinutes = 0
        Events.on<ServerTickEvent> {
            dispatch(TickPeriod())
            currentTicks++

            // Seconds
            if(currentTicks == 20) {
                currentTicks = 0
                currentSeconds++
                dispatch(SecondPeriod())
            }

            // Minutes
            if(currentSeconds == 60) {
                currentSeconds = 0
                currentMinutes++
                dispatch(MinutePeriod())
            }

            // Hours
            if(currentMinutes == 60) {
                currentMinutes = 0
                dispatch(HourPeriod())
            }
        }
    }
}

class ExecutablePeriodic<T : Periodic>(
    val function: (event: T) -> Unit
)