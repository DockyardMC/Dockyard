package io.github.dockyardmc

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerTickEvent
import io.github.dockyardmc.periodic.Period
import io.github.dockyardmc.periodic.SecondPeriod
import java.time.Instant

object ServerMetrics {
    var millisecondsPerTick: Double = 0.0

    var asyncQueueProcessorTasks: Int = 0
    var packetsSent: Int = 0
    var packetsReceived: Int = 0

    var packetsSentPerSecond = mutableListOf<Int>()
    var packetsReceivedPerSecond = mutableListOf<Int>()

    var packetsSentAverage = 0
    var packetsReceivedAverage = 0

    init {
        val averages = mutableListOf<Long>(50)
        var timeSinceLastTick = Instant.now()
        Period.on<SecondPeriod> {
            averages.clear()
            averages.add(50)

            packetsSentAverage = packetsSentPerSecond.sum() / packetsSentPerSecond.size
            packetsReceivedAverage = packetsReceivedPerSecond.sum() / packetsReceivedPerSecond.size

            packetsSentPerSecond.clear()
            packetsReceivedPerSecond.clear()

            packetsSent = 0
            packetsReceived = 0
        }

        Events.on<ServerTickEvent> {
            packetsSentPerSecond.add(packetsSent)
            packetsReceivedPerSecond.add(packetsReceived)
            val diff = Instant.now().toEpochMilli() - timeSinceLastTick.toEpochMilli()
            averages.add(diff)
            timeSinceLastTick = Instant.now()
            millisecondsPerTick = Math.round(averages.average() * 10) / 10.0
        }
    }
}