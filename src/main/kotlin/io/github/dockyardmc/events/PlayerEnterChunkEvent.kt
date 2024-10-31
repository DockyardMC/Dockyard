package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.world.Chunk

@EventDocumentation("when player enters new chunk", false)
class PlayerEnterChunkEvent(val chunkIndex: Long, val chunk: Chunk?, val player: Player): Event {
    override val context = Event.Context(players = setOf(player), worlds = setOfNotNull(chunk?.world))
}