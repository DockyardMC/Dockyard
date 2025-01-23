package io.github.dockyardmc.server

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerTickEvent
import io.github.dockyardmc.extentions.truncate
import io.github.dockyardmc.periodic.Period
import io.github.dockyardmc.periodic.SecondPeriod
import io.github.dockyardmc.utils.percent

object ServerMetrics {
    var packetsSent: Int = 0
    var packetsReceived: Int = 0

    private val runtime = Runtime.getRuntime()

    private var packetsSentPerSecond = mutableListOf<Int>()
    private var packetsReceivedPerSecond = mutableListOf<Int>()

    var packetsSentAverage = 0
    var packetsReceivedAverage = 0

    val mspt get() = DockyardServer.scheduler.mspt

    val memoryUsage get() = runtime.totalMemory() - runtime.freeMemory()
    val memoryUsagePercent get() = percent(runtime.totalMemory().toDouble(), memoryUsage.toDouble())
    val memoryRented get() = runtime.totalMemory()
    val memoryAllocated get() = runtime.maxMemory()

    val memoryUsageTruncated get() = (memoryUsage.toDouble() / 1000000).truncate(1)
    val memoryRentedTruncated get() = (memoryRented.toDouble() / 1000000).truncate(1)
    val memoryAllocatedTruncated get() = (memoryAllocated.toDouble() / 1000000).truncate(1)

    init {
        var seconds = 0
        Period.on<SecondPeriod> {
            seconds++

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
        }
    }
}