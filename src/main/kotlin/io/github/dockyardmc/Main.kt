package io.github.dockyardmc

import CustomLogType
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PacketReceivedEvent
import io.github.dockyardmc.events.PlayerConnectEvent
import io.github.dockyardmc.events.PlayerMoveEvent
import io.github.dockyardmc.extentions.truncate
import io.github.dockyardmc.periodic.HourPeriod
import io.github.dockyardmc.periodic.MinutePeriod
import io.github.dockyardmc.periodic.Period
import io.github.dockyardmc.periodic.TickPeriod
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundPlayerChatMessagePacket
import io.github.dockyardmc.utils.MathUtils

val TCP = CustomLogType("\uD83E\uDD1D TCP", AnsiPair.GRAY)

object Main {
    lateinit var instance: DockyardServer
}

fun main(args: Array<String>) {
    val port = (args.getOrNull(0) ?: "25565").toInt()
    Main.instance = DockyardServer(port)

    Events.on<PlayerConnectEvent> {
        DockyardServer.broadcastMessage("<lime>→ <yellow>${it.player}")
    }

    Period.on<TickPeriod> {
        val runtime = Runtime.getRuntime()
        val mspt = ServerMetrics.millisecondsPerTick
        val memoryUsage = runtime.totalMemory() - runtime.freeMemory()
        val memUsagePercent = MathUtils.percent(runtime.totalMemory().toDouble(), memoryUsage.toDouble()).truncate(0)

        val fMem = (memoryUsage.toDouble() / 1000000).truncate(1)
        val fMax = (runtime.totalMemory().toDouble() / 1000000).truncate(1)
        DockyardServer.broadcastActionBar("<white>MSPT: <lime>$mspt <dark_gray>| <white>Memory Usage: <#ff6830>$memUsagePercent% <gray>(${fMem}mb / ${fMax}mb)")
    }

    Main.instance.start()
}