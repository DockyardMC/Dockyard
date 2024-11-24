package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player

@EventDocumentation("when group or item cooldown ends for a player", false)
class ItemGroupCooldownEndEvent(val player: Player, val cooldown: Player.ItemGroupCooldown, override val context: Event.Context): Event {
}