package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player

@EventDocumentation("when group or item cooldown starts for a player", true)
class ItemGroupCooldownStartEvent(val player: Player, var cooldown: Player.ItemGroupCooldown, override val context: Event.Context) : CancellableEvent()