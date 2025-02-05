package io.github.dockyardmc.events

import io.github.dockyardmc.player.Player

class PlayerLoadedEvent(val player: Player, override val context: Event.Context) : Event