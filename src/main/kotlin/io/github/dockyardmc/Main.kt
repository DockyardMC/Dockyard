package io.github.dockyardmc

import CustomLogType
import io.github.dockyardmc.Entity.EntityRegistry
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.events.PlayerLeaveEvent
import io.github.dockyardmc.extentions.truncate
import io.github.dockyardmc.periodic.Period
import io.github.dockyardmc.periodic.TickPeriod
import io.github.dockyardmc.player.*
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundPlayerInfoUpdatePacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundSpawnEntityPacket
import io.github.dockyardmc.utils.MathUtils
import io.github.dockyardmc.utils.Velocity

val TCP = CustomLogType("\uD83E\uDD1D TCP", AnsiPair.GRAY)

object Main {
    lateinit var instance: DockyardServer
}

fun main(args: Array<String>) {
    val port = (args.getOrNull(0) ?: "25565").toInt()
    Main.instance = DockyardServer(port)

    Events.on<PlayerJoinEvent> {
        DockyardServer.broadcastMessage("<lime>→ <yellow>${it.player}")

        // send this player to existing players
        PlayerManager.players.forEach { loopPlayer ->
            if(loopPlayer.username == it.player.username) return@forEach

            val fromInfo = PlayerInfoUpdate(it.player.uuid, AddPlayerInfoUpdateAction(PlayerUpdateProfileProperty(it.player.username, mutableListOf(it.player.profile!!.properties[0]))))
            val fromPacket = ClientboundPlayerInfoUpdatePacket(0x01, mutableListOf(fromInfo))
            loopPlayer.sendPacket(fromPacket)

            val fromEntitySpawnPacket = ClientboundSpawnEntityPacket(it.player.entityId!!, it.player.uuid, EntityRegistry.PLAYER.id + 2, it.player.location, 0f, 0, Velocity(0, 0, 0))
            val toEntitySpawnPacket = ClientboundSpawnEntityPacket(loopPlayer.entityId!!, loopPlayer.uuid, EntityRegistry.PLAYER.id + 2, loopPlayer.location, 0f, 0, Velocity(0, 0, 0))

            val toInfo = PlayerInfoUpdate(loopPlayer.uuid, AddPlayerInfoUpdateAction(PlayerUpdateProfileProperty(loopPlayer.username, mutableListOf(loopPlayer.profile!!.properties[0]))))
            val toPacket = ClientboundPlayerInfoUpdatePacket(0x01, mutableListOf(toInfo))
            it.player.sendPacket(toPacket)

            it.player.sendPacket(toEntitySpawnPacket)
            loopPlayer.sendPacket(fromEntitySpawnPacket)
        }

        // send existing players to this player
    }

    Events.on<PlayerLeaveEvent> {
        DockyardServer.broadcastMessage("<red>← <yellow>${it.player}")
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