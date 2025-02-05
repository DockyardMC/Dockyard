package io.github.dockyardmc.events

import io.github.dockyardmc.player.Player

class InventoryClickEvent(val player: Player, override val context: Event.Context): Event {
}