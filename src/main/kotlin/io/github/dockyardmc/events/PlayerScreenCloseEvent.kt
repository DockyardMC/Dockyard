package io.github.dockyardmc.events

import io.github.dockyardmc.player.Player
import io.github.dockyardmc.ui.Screen

data class PlayerScreenCloseEvent(val player: Player, val screen: Screen, override val context: Event.Context) : Event