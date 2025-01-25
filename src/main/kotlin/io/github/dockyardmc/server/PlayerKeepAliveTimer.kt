package io.github.dockyardmc.server

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundKeepAlivePacket
import io.github.dockyardmc.scheduler.SchedulerTask
import kotlin.time.Duration.Companion.seconds

class PlayerKeepAliveTimer {

    var currentKeepAlive = 0L
    lateinit var task: SchedulerTask

    fun start() {
        task = DockyardServer.scheduler.runRepeating(10.seconds) {
            PlayerManager.players.filter { it.networkManager.state == ProtocolState.PLAY }.forEach { player ->
                player.sendPacket(ClientboundKeepAlivePacket(currentKeepAlive))
                if (!player.networkManager.respondedToLastKeepAlive) {
                    player.kick("Keep alive")
                    return@forEach
                }
                player.networkManager.respondedToLastKeepAlive = false
            }
            currentKeepAlive++
        }
    }
}