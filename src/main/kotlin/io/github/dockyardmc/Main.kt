package io.github.dockyardmc

import CustomLogType
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.EnumArgument
import io.github.dockyardmc.commands.PlayerArgument
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.events.PlayerLeaveEvent
import io.github.dockyardmc.events.PlayerMoveEvent
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.extentions.truncate
import io.github.dockyardmc.periodic.Period
import io.github.dockyardmc.periodic.TickPeriod
import io.github.dockyardmc.player.*
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundPlayerGameEventPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundUpdateEntityPositionPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.GameEvent
import io.github.dockyardmc.utils.MathUtils

val TCP = CustomLogType("\uD83E\uDD1D TCP", AnsiPair.GRAY)

object Main {
    lateinit var instance: DockyardServer
}

fun main(args: Array<String>) {
    val port = (args.getOrNull(0) ?: "25565").toInt()
    Main.instance = DockyardServer(port)


    //TODO: Move to different class (probably player)
    Events.on<PlayerJoinEvent> {
        DockyardServer.broadcastMessage("<lime>→ <yellow>${it.player}")

        // send this player to existing players
        PlayerManager.players.forEach { loopPlayer ->
            if(loopPlayer.username == it.player.username) return@forEach
            it.player.addViewer(loopPlayer)
            loopPlayer.addViewer(it.player)
        }
    }

    //TODO: Move to different class (probably player)
    Events.on<PlayerLeaveEvent> {
        DockyardServer.broadcastMessage("<red>← <yellow>${it.player}")
    }


    //TODO: Move to somewhere else (probably player class)
    Events.on<PlayerMoveEvent> { event ->
        val player = event.player
        val packet = ClientboundUpdateEntityPositionPacket(player, event.oldLocation)
        player.viewers.forEach { it.sendPacket(packet) }
    }

    Main.instance.start()
}