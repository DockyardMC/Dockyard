package io.github.dockyardmc.utils

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerTickEvent
import io.github.dockyardmc.extentions.truncate
import io.github.dockyardmc.server.ServerMetrics
import io.github.dockyardmc.apis.sidebar.Sidebar
import java.lang.management.GarbageCollectorMXBean
import java.lang.management.ManagementFactory

object DebugSidebar {

    val sidebar = Sidebar {
        setTitle("<#fc4903><bold>Debug Sidebar")
        setGlobalLine(15, "                                      ")
        setGlobalLine(0, " ")
    }

    init {
        var tickCounter = 0
        Events.on<ServerTickEvent> {
            val runtime = Runtime.getRuntime()
            val globalMspt = DockyardServer.scheduler.mspt

            val msptColor = when {
                globalMspt >= 60.0 -> "<red>"
                globalMspt >= 58.0 -> "<orange>"
                globalMspt > 50.0 -> "<yellow>"
                globalMspt == 50.0 -> "<lime>"
                else -> "<dark_red>"
            }

            val memoryPercentColor = when {
                ServerMetrics.memoryUsagePercent <= 50.0 -> "<lime>"
                ServerMetrics.memoryUsagePercent <= 70.0 -> "<yellow>"
                ServerMetrics.memoryUsagePercent <= 80.0 -> "<orange>"
                ServerMetrics.memoryUsagePercent <= 90.0 -> "<red>"
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

            sidebar.setGlobalLine(14, " MSPT:")
            sidebar.setGlobalLine(13, " ◾ Global: $msptColor${globalMspt}ms")
            sidebar.setPlayerLine(12) {
                var worldSchedulerColor = if(it.world.scheduler.mspt == it.world.scheduler.tickRate.value.inWholeMilliseconds.toDouble()) "<lime>" else "<orange>"
                if(it.world.scheduler.paused.value) worldSchedulerColor = "<red>"

                " ◾ World: ${worldSchedulerColor}${it.world.scheduler.mspt}ms"
            }
            sidebar.setGlobalLine(11, " ")
            sidebar.setGlobalLine(10, " Memory: $memoryPercentColor${ServerMetrics.memoryUsagePercent.truncate(1)}%")
            sidebar.setGlobalLine(9, " ◾ Using $memoryPercentColor${ServerMetrics.memoryUsageTruncated}mb")
            sidebar.setGlobalLine(8, " ◾ Rented <aqua>${ServerMetrics.memoryRentedTruncated}mb")
            sidebar.setGlobalLine(7, " ◾ Allocated <aqua>${ServerMetrics.memoryAllocatedTruncated}mb")
            sidebar.setGlobalLine(6, " ")
            sidebar.setGlobalLine(5, " Packets: <#cba3ff>↑${ServerMetrics.packetsSentAverage} ↓${ServerMetrics.packetsReceivedAverage}")
            sidebar.setGlobalLine(4, " ")
            sidebar.setGlobalLine(4, " Event Listeners: <lime>$totalEvents")
            sidebar.setGlobalLine(2, " gc collections: <orange>${totalCollections}")
            sidebar.setGlobalLine(1, " gc time: <orange>${totalCollectionTime}ms")
        }
    }
}