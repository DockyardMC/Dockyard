package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.world.chunk.ChunkPos

@EventDocumentation("when player enters new chunk")
data class PlayerEnterChunkEvent(val oldChunkPos: ChunkPos, val newChunkPos: ChunkPos?, val player: Player, override val context: Event.Context) : Event