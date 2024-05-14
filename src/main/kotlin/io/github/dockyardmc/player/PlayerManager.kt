package io.github.dockyardmc.player

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerConnectEvent
import io.github.dockyardmc.protocol.PacketProcessor
import java.util.UUID

object PlayerManager {

    val players: MutableList<Player> = mutableListOf()
    val playerToProcessorMap = mutableMapOf<UUID, PacketProcessor>()

    fun add(player: Player, processor: PacketProcessor) {
        players.add(player)
        playerToProcessorMap[player.uuid] = processor
        processor.player = player
        Events.dispatch(PlayerConnectEvent(player))
    }

    fun remove(player: Player) {
        players.remove(player)
        playerToProcessorMap.remove(player.uuid)
    }

}