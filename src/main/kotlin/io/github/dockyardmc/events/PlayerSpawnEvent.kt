package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.world.World

@EventDocumentation("when player is in configuration phase and needs initial world to spawn in")
data class PlayerSpawnEvent(val player: Player, var world: World, override val context: Event.Context) : Event