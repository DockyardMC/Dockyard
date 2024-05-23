package io.github.dockyardmc

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerTickEvent
import io.github.dockyardmc.extentions.truncate
import io.github.dockyardmc.periodic.Period
import io.github.dockyardmc.periodic.SecondPeriod
import java.time.Instant

object ServerMetrics {
    var millisecondsPerTick: Double = 0.0

    init {
        val averages = mutableListOf<Long>(50)
        var timeSinceLastTick = Instant.now()
        Period.on<SecondPeriod> {
            averages.clear()
            averages.add(50)
        }

        Events.on<ServerTickEvent> {
            val diff = Instant.now().toEpochMilli() - timeSinceLastTick.toEpochMilli()
            averages.add(diff)
            timeSinceLastTick = Instant.now()
            millisecondsPerTick = averages.average().truncate(1).toDouble()
            if(millisecondsPerTick < 50) millisecondsPerTick = 50.0
        }
    }
}