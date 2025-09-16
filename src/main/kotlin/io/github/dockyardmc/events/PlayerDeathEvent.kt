package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player

@EventDocumentation("when player dies")
data class PlayerDeathEvent(val player: Player, override val context: Event.Context) : CancellableEvent()