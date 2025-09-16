package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.ui.Screen

@EventDocumentation("when screen (inventory gui) is closed")
data class PlayerScreenCloseEvent(val player: Player, val screen: Screen, override val context: Event.Context) : Event