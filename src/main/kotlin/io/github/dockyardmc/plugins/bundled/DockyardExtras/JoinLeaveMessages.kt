package io.github.dockyardmc.plugins.bundled.DockyardExtras

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.events.PlayerLeaveEvent

class JoinLeaveMessages {

    fun register() {
        Events.on<PlayerJoinEvent> {
            DockyardServer.broadcastMessage("<lime>→ <yellow>${it.player}")
        }

        Events.on<PlayerLeaveEvent> {
            DockyardServer.broadcastMessage("<red>← <yellow>${it.player}")
            log("${it.player} Disconnected, last packet: ${it.player.lastSentPacket::class.simpleName}", LogType.FATAL)
        }
    }
}