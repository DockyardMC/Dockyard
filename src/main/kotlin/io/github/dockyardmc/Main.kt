package io.github.dockyardmc

import CustomLogType
import io.github.dockyardmc.commands.nodes.Commands
import io.github.dockyardmc.commands.nodes.EnumArgument
import io.github.dockyardmc.commands.nodes.PlayerArgument
import io.github.dockyardmc.entity.EntityType
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.events.PlayerLeaveEvent
import io.github.dockyardmc.events.PlayerMoveEvent
import io.github.dockyardmc.extentions.truncate
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.periodic.Period
import io.github.dockyardmc.periodic.TickPeriod
import io.github.dockyardmc.player.*
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundUpdateEntityPositionPacket
import io.github.dockyardmc.utils.MathUtils
import io.github.dockyardmc.world.WorldManager
import log

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
            it.player.addViewer(loopPlayer)
            loopPlayer.addViewer(it.player)
        }
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

    Events.on<PlayerMoveEvent> { event ->
        val player = event.player
        val packet = ClientboundUpdateEntityPositionPacket(player, event.oldLocation)
        player.viewers.forEach { it.sendPacket(packet) }
    }

    Commands.add("setPose") { command ->
        command.addChild("player", PlayerArgument())
        command.addChild("pose", EnumArgument(EntityPose::class))
        command.execute {
            val player = command.get<Player>("player")
            val pose = command.getEnum<EntityPose>("pose")

            player.pose.value = pose
            it.player!!.sendMessage("<yellow>Set pose of player <lime>$player <yellow>to <aqua>${pose.name}")
        }
    }

    Main.instance.start()
}