package io.github.dockyardmc.server

import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.player.kick.KickReason
import io.github.dockyardmc.player.kick.getSystemKickMessage
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundKeepAlivePacket
import io.github.dockyardmc.runnables.RepeatingTimerAsync

class PlayerKeepAliveTimer {

    var currentKeepAlive = 0L
    val interval: Long = 10000

    val timer = RepeatingTimerAsync(interval) {
        PlayerManager.players.filter { it.networkManager.state == ProtocolState.PLAY }.forEach { player ->
            player.sendPacket(ClientboundKeepAlivePacket(currentKeepAlive))
            if (!player.networkManager.respondedToLastKeepAlive) {
                player.kick(getSystemKickMessage(KickReason.FAILED_KEEP_ALIVE))
                return@forEach
            }
            player.networkManager.respondedToLastKeepAlive = false
        }
        currentKeepAlive++
    }

    fun start() {
        timer.run()
    }
}