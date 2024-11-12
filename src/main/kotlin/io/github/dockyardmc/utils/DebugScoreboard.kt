package io.github.dockyardmc.utils

import io.github.dockyardmc.server.ServerMetrics
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerTickEvent
import io.github.dockyardmc.extentions.truncate
import io.github.dockyardmc.sidebar.Sidebar
import java.lang.management.GarbageCollectorMXBean
import java.lang.management.ManagementFactory

object DebugScoreboard {

    val sidebar = Sidebar("<#fc4903><bold>Debug Sidebar") {
        setGlobalLine(15, "                                      ")
        setGlobalLine(0, " ")
    }

    init {
        var tickCounter = 0
        Events.on<ServerTickEvent> {
            val runtime = Runtime.getRuntime()
            val mspt = ServerMetrics.millisecondsPerTick
            val memoryUsage = runtime.totalMemory() - runtime.freeMemory()
            val memUsagePercent = percent(runtime.totalMemory().toDouble(), memoryUsage.toDouble())

            val fMem = (memoryUsage.toDouble() / 1000000).truncate(1)
            val fRented = (runtime.totalMemory().toDouble() / 1000000).truncate(1)
            val fMax = (runtime.maxMemory().toDouble() / 1000000).truncate(1)


            val msptColor = when {
                mspt >= 60.0 -> "<red>"
                mspt >= 58.0 -> "<orange>"
                mspt > 50.0 -> "<yellow>"
                mspt == 50.0 -> "<lime>"
                else -> "<dark_red>"
            }

            val memoryPercentColor = when {
                memUsagePercent <= 50.0 -> "<lime>"
                memUsagePercent <= 70.0 -> "<yellow>"
                memUsagePercent <= 80.0 -> "<orange>"
                memUsagePercent <= 90.0 -> "<red>"
                else -> "<dark_red>"
            }

            var totalCollections = 0L
            var totalCollectionTime = 0L
            var memPoolTotal = 0

            val gcBeans: List<GarbageCollectorMXBean> = ManagementFactory.getGarbageCollectorMXBeans()
            for (gcBean in gcBeans) {
                val count = gcBean.collectionCount
                val time = gcBean.collectionTime
                val memPool = gcBean.memoryPoolNames.size

                totalCollections += count
                totalCollectionTime += time
                memPoolTotal += memPool
            }

            var totalEvents: Int = 0
            Events.eventMap.values.forEach {
                totalEvents += it.list.size
            }

            sidebar.setGlobalLine(14, " Ms per tick: ${msptColor}${mspt}ms")
            sidebar.setGlobalLine(13, " Memory: $memoryPercentColor${memUsagePercent.truncate(1)}%")
            sidebar.setGlobalLine(12, " ◾ Using $memoryPercentColor${fMem}mb")
            sidebar.setGlobalLine(11, " ◾ Rented <aqua>${fRented}mb")
            sidebar.setGlobalLine(10, " ◾ Allocated <aqua>${fMax}mb")
            sidebar.setGlobalLine(9, " ")
            sidebar.setGlobalLine(8, " AsyncQueueProcessor: <#cba3ff>${ServerMetrics.asyncQueueProcessorTasks}")
            sidebar.setGlobalLine(6, " Packets: <#cba3ff>↑${ServerMetrics.packetsSentAverage} ↓${ServerMetrics.packetsReceivedAverage}")
            sidebar.setGlobalLine(5, " ")
            sidebar.setGlobalLine(4, " Event Listeners: <lime>$totalEvents")
            sidebar.setGlobalLine(3, " gc collections: <orange>${totalCollections}")
            sidebar.setGlobalLine(2, " gc time: <orange>${totalCollectionTime}ms")
        }
    }
}