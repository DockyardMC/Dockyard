package io.github.dockyardmc.player

import io.github.dockyardmc.entities.EntityManager
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerConnectEvent
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.world.World

object PlayerManager {

    val innerPlayers: MutableList<Player> = mutableListOf()
    val players get() = innerPlayers.toList()

    private val innerPlayerToEntityIdMap = mutableMapOf<Int, Player>()
    val playerToEntityIdMap get() = innerPlayerToEntityIdMap.toMap()

    fun add(player: Player, processor: PlayerNetworkManager) {
        synchronized(innerPlayers) {
            innerPlayers.add(player)
        }
        synchronized(innerPlayerToEntityIdMap) {
            innerPlayerToEntityIdMap[player.entityId] = player
        }

        processor.player = player
        EntityManager.addPlayer(player)
        Events.dispatch(PlayerConnectEvent(player))
    }

    fun remove(player: Player) {
        player.isConnected = false
        player.viewers.toList().forEach {
            player.removeViewer(it, true);
            it.removeViewer(player, true)
        }

        synchronized(innerPlayers) {
            innerPlayers.remove(player)
        }
        synchronized(playerToEntityIdMap) {
            innerPlayerToEntityIdMap.remove(player.entityId)
        }
        player.world.removePlayer(player)
        player.world.removeEntity(player)
        player.dispose()
    }

    fun sendToEveryoneInWorld(world: World, packet: ClientboundPacket) {
        val filtered = players.filter { it.world == world }
        filtered.forEach { it.sendPacket(packet) }
    }
}