package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player
@EventDocumentation("player connects to the server, before joining", false)
class PlayerConnectEvent(val player: Player): Event {
}