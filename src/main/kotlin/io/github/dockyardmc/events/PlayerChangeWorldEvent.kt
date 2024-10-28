package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.world.World

@EventDocumentation("when player changes worlds", false)
class PlayerChangeWorldEvent(val player: Player, val oldWorld: World, val newWorld: World): Event {
    override val context = elements(player, oldWorld, newWorld)
}