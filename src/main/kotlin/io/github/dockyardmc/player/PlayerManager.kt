package io.github.dockyardmc.player

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerConnectEvent
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.world.World
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

object PlayerManager {

    val players: MutableList<Player> = mutableListOf()
    val playerToProcessorMap = mutableMapOf<UUID, PacketProcessor>()
    val playerToEntityIdMap = mutableMapOf<Int, Player>()

    fun add(player: Player, processor: PacketProcessor) {
        players.add(player)
        playerToProcessorMap[player.uuid] = processor
        playerToEntityIdMap[player.entityId] = player
        processor.player = player
        Events.dispatch(PlayerConnectEvent(player))
    }

    fun remove(player: Player) {
        player.viewers.toMutableList().forEach { player.removeViewer(it, true) }

        players.remove(player)
        playerToProcessorMap.remove(player.uuid)
        playerToEntityIdMap.remove(player.entityId)
    }

    fun sendToEveryoneInWorld(world: World, packet: ClientboundPacket) {
        val filtered = players.filter { it.world == world }
        filtered.forEach { it.connection.sendPacket(packet) }
    }
}