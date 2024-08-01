package io.github.dockyardmc.plugins.bundled.extras

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.events.PlayerLeaveEvent
import io.github.dockyardmc.extentions.broadcastMessage

class JoinLeaveMessages {

    fun register() {
        Events.on<PlayerJoinEvent> {
            DockyardServer.broadcastMessage("<yellow>${it.player} joined the game.")
        }

        Events.on<PlayerLeaveEvent> {
            DockyardServer.broadcastMessage("<yellow>${it.player} left the game.")
        }
    }
}