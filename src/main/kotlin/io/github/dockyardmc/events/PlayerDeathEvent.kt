package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player

@EventDocumentation("when player dies", true)
class PlayerDeathEvent(val player: Player): CancellableEvent() {
    override val context = elements(player)
}