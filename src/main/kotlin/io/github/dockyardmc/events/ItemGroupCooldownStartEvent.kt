package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.systems.ItemGroupCooldown

@EventDocumentation("when group or item cooldown starts for a player")
data class ItemGroupCooldownStartEvent(val player: Player, var cooldown: ItemGroupCooldown, override val context: Event.Context) : CancellableEvent()