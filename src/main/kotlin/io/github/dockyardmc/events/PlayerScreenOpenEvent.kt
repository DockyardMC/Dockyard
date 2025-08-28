package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.ui.Screen

@EventDocumentation("when a screen (inventory gui) is open to a player")
data class PlayerScreenOpenEvent(val player: Player, val screen: Screen, override val context: Event.Context) : CancellableEvent()